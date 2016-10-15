package hu.elte.szakdolgozat.rest.services;

import hu.elte.szakdolgozat.model.User;
import hu.elte.szakdolgozat.services.DuplicateException;
import hu.elte.szakdolgozat.services.ValidationException;
import hu.elte.szakdolgozat.services.InfrastructureException;
import hu.elte.szakdolgozat.services.UserManagerService;
import java.util.UUID;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@Path("/UserManagerRestService")
public class UserManagerRestService {

    private final UserManagerService ums = UserManagerService.getInstance();
    Logger logger = LoggerFactory.getLogger(UserManagerRestService.class);

    @PUT
    @Path("/user")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(User user) {
        final String requestId = UUID.randomUUID().toString();
        MDC.put("RequestId", requestId);
        try {
            ums.createUser(user);
            return Response.status(Response.Status.OK).build();
        } catch (ValidationException ve) {
            logger.error("unable to create user", ve);
            return Response.status(Response.Status.BAD_REQUEST).entity(ve.getMessage()).
                    type("text/plain").header("X-Request-Id", requestId).build();
        } catch (DuplicateException de) {
            logger.error("unable to create user", de);
            return Response.status(Response.Status.BAD_REQUEST).entity(de.getMessage()).
                    type("text/plain").header("X-Request-Id", requestId).build();
        } catch (InfrastructureException ie) {
            logger.error("unable to create user", ie);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(ie.getMessage()).
                    type("text/plain").header("X-Request-Id", requestId).build();
        } catch (RuntimeException re) {
            logger.error("critical error", re);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(re.getMessage()).
                    type("text/plain").header("X-Request-Id", requestId).build();
        }
    }

    @GET
    @Path("/user/{userName}")
    @Produces(MediaType.TEXT_PLAIN + ";charset=utf-8")
    public Response getUser(@PathParam("userName") String userName) {
        final String requestId = UUID.randomUUID().toString();
        MDC.put("RequestId", requestId);
        try {
            String realName = ums.getUser(userName);
            if (null == realName) {
                return Response.status(Response.Status.NOT_FOUND).
                        entity("User not found for username: " + userName).build();
            }
            return Response.status(Response.Status.OK).entity(realName).build();
        } catch (InfrastructureException ie) {
            logger.error("unable to execute query", ie);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).
                    entity(ie.getMessage()).header("X-Request-Id", requestId).build();
        } catch (RuntimeException re) {
            logger.error("critical error", re);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity(re.getMessage()).header("X-Request-Id", requestId).build();
        }
    }

    @GET
    @Path("/user/list")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getUsers() {
        final String requestId = UUID.randomUUID().toString();
        MDC.put("RequestId", requestId);
        try {
            return Response.status(Response.Status.OK).entity(ums.getUsers()).build();
        } catch (InfrastructureException ie) {
            logger.error("unable to execute query", ie);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(ie.getMessage()).
                    type("text/plain").header("X-Request-Id", requestId).build();
        } catch (RuntimeException re) {
            logger.error("critical error", re);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(re.getMessage()).
                    type("text/plain").header("X-Request-Id", requestId).build();
        }
    }

    @DELETE
    @Path("/user/{id}")
    public Response deleteUser(@PathParam("id") Integer id) {
        final String requestId = UUID.randomUUID().toString();
        MDC.put("RequestId", requestId);
        try {
            ums.deleteUser(id);
            return Response.noContent().build();
        } catch (InfrastructureException ie) {
            logger.error("unable to delete user", ie);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(ie.getMessage()).
                    type("text/plain").header("X-Request-Id", requestId).build();
        } catch (RuntimeException re) {
            logger.error("critical error", re);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(re.getMessage()).
                    type("text/plain").header("X-Request-Id", requestId).build();
        }
    }
}
