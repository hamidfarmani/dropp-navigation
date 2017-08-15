package service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import model.entity.persistent.Operator;
import model.enums.AccountState;
import model.enums.Status;
import model.enums.UserRole;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import security.jwt.JwtService;
import security.model.AccountCredentials;
import util.IOCContainer;
import util.LocalEntityManagerFactory;
import util.converter.UserRoleConverter;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;


@Service("systemService")
@Scope(value = "singleton", proxyMode = ScopedProxyMode.INTERFACES)
public class SystemServiceImpl implements SystemService {


    @Override
    public Object refreshToken(String oldToken) {
        // parse the token.
        Claims claims = Jwts.parser()
                .setSigningKey(JwtService.SECRET)
                .parseClaimsJws(oldToken)
                .getBody();
        String username = claims.getSubject();
        String role = claims.get("role").toString();
        if (username == null || username.equals("") || role.equals("") || role == null) {
            return Status.UNAUTHORIZED;
        }
        UserRole userRole = ((UserRoleConverter) IOCContainer.getBean("userRoleConverter")).convertToEntityAttribute(role.charAt(0));
        Status status;
        switch (userRole) {
            case OPERATOR:
                status = validateOperatorForNewToken(username);
                break;
            case MASTER_OPERATOR:
                status = validateOperatorForNewToken(username);
                break;
            case ADMIN:
                status = validateOperatorForNewToken(username);
                break;
            case CAR_OPERATOR:
                status = validateOperatorForNewToken(username);
                break;
            case PROVIDER:
                status = validateOperatorForNewToken(username);
                break;
            default:
                throw new IllegalArgumentException("Unkown " + userRole);
        }
        if (status != Status.OK) {
            return status;
        }
        AccountCredentials accountCredentials = new AccountCredentials(username, role);
        String newToken = JwtService.addAuthentication(accountCredentials);
        return newToken;
    }

    public Status validateOperatorForNewToken(String username) {
        EntityManager entityManager = LocalEntityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Operator operator = (Operator) entityManager.createNamedQuery("operator.exact.username")
                    .setParameter("username", username)
                    .setMaxResults(1)
                    .getSingleResult();
            if (operator.getAccountState() == AccountState.BANNED) {
                return Status.USER_BANNED;
            }
//            if (operator.isLoggedIn() == false) {
//                return Status.USER_NOT_LOGGED_IN;
//            }
            entityManager.getTransaction().commit();
            return Status.OK;
        } catch (NoResultException e) {
            return Status.NOT_FOUND;
        } catch (Exception e) {
            return Status.UNKNOWN_ERROR;
        } finally {
            if(entityManager.getTransaction().isActive()){
                entityManager.getTransaction().rollback();
            }if(entityManager.isOpen()){
                entityManager.close();
            }
        }
    }
}
