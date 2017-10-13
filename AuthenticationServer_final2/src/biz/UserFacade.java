package biz;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import entity.User_Roles;
import entity.Users;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Facade to communicate with MySQL
 *
 * @author Keyvan Derakhshan Nik
 */
public class UserFacade {

    /**
     * Create em1 , em2, em3, em4 for different Targets em1 --> find User em2
     * --> set Password in add User em3 --> add UserName and Password in add
     * User em4 --> add UserName and Role in Add User
     */
    private final EntityManager em1, em2, em3, em4,em5;

    /**
     * Create UserFacade for connecting to MyDB in MySQL
     */
    public UserFacade() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("mydb");
        this.em1 = emf.createEntityManager();
        this.em2 = emf.createEntityManager();
        this.em3 = emf.createEntityManager();
        this.em4 = emf.createEntityManager();
        this.em5 = emf.createEntityManager();
    }

    /**
     * This Method Find User by its useName and password from database if there
     * is no result returns false else return true
     */
    public boolean findUser(String userName, String password) {
        Users u;
        // select query
        String jpql1 = "select u from Users u where u.user_name  like :userName";
        Query q1 = em1.createQuery(jpql1);
        q1.setParameter("userName", userName);
        try {
            u = (Users) q1.getSingleResult();
            if ((u != null) & (encrypt(u.getUser_pass()).equals(password))) {
                System.out.println("I found it");
                return true;
            }
        } catch (Exception e) {
            System.out.println("Account Not Found!");
        }
        em1.close();
        return false;
    }

    /**
     * To Encrypt a String (temp) for encryption the password
     */
    private String encrypt(String temp) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(temp.getBytes());
            byte[] digest = md.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return "error";
        }
    }

    /**
     * This method returns all of Roles of user which is available on DB
     */
    public String findRoles(String user) {
        String role;
        // select query
        String jpql2 = "select u.role_name from User_Roles u where u.user_name  like :userName";
        Query q2 = em2.createQuery(jpql2);
        q2.setParameter("userName", user);
        try {
            role = q2.getResultList().toString();
            em2.close();
            return role;
        } catch (Exception e) {
            System.out.println("Problem to find Role");
            return null;
        }
    }

    /**
     * This Method add UserName , Password and UserName and Password to tables
     * If there are not exist on the Table . In addition It can change Password
     * of User if the user is available on Table
     */
    public String addUser(String u, String p, String r) {
        //response for users table return to this 
        Users u1;
        //response for user_roles table return to this 
        User_Roles ur1;
        //String which must send back as a result
        StringBuilder response = new StringBuilder();
        // select query
        String findUser = "select u from Users u where u.user_name  like :userName";
        // select query
        String findRoles = "select ur from User_Roles ur where (ur.user_name  like :userName and ur.role_name like :roleName)";
        try {
            Users u2 = new Users();
            u2.setUser_name(u);
            u2.setUser_pass(p);
            em3.getTransaction().begin();
            em3.persist(u2);
            em3.getTransaction().commit();
            em3.close();
            response.append("userName and password  are added ");
        } catch (Exception e) {
                Query q2 = em4.createQuery(findUser);
                q2.setParameter("userName", u);
                u1 = (Users) q2.getSingleResult();
                em4.close();
                if (u1.getUser_name().equals(u)) {
                    if (u1.getUser_pass().equals(p)) {
                        response.append("UserName and Password are available,");
                    } else {
                        em5.getTransaction().begin();
                        //em5.flush();
                        u1.setUser_pass(p);
                        em5.merge(u1);
                        em5.flush();
                        em5.getTransaction().commit();
                        em5.close();
                        response.append(" The userName was available and The password is changed !! ");
                    }
                }
               
            
        }
            Query q4 = em1.createQuery(findRoles);
            q4.setParameter("userName", u);
            q4.setParameter("roleName", r);
            try {
                em1.getTransaction().begin();
                User_Roles er = new User_Roles();
                er.setRole_name(r);
                er.setUser_name(u);
                em1.persist(er);
                em1.getTransaction().commit();
                em1.close();
                response.append(" But the role is attached");
            } catch (Exception e) {
                ur1 = (User_Roles) q4.getSingleResult();
                if ((ur1.getUser_name().equals(u)) & (ur1.getRole_name().equals(r))) {
                    response.append(" the role is available");
                }
            }
            return response.toString();
        }
    }
