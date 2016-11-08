package hu.elte.szakdolgozat.rest.services;

import hu.elte.szakdolgozat.model.User;
import hu.elte.szakdolgozat.services.ValidationException;
import hu.elte.szakdolgozat.services.InfrastructureException;
import hu.elte.szakdolgozat.services.UserManagerService;
import java.util.UUID;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
    @Produces(MediaType.TEXT_PLAIN + ";charset=utf-8")
    public Response createUser(User user) {
        final String requestId = UUID.randomUUID().toString();
        MDC.put("RequestId", requestId);
        try {
            ums.createUser(user);
            return Response.status(Response.Status.OK).build();
        } catch (ValidationException ve) {
            logger.error("unable to create user", ve);
            return Response.status(Response.Status.BAD_REQUEST).
                    entity(ve.getMessage()).header("X-Request-Id", requestId).build();
        } catch (InfrastructureException ie) {
            logger.error("unable to create user", ie);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).
                    entity(ie.getMessage()).header("X-Request-Id", requestId).build();
        } catch (RuntimeException re) {
            logger.error("critical error", re);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity(re.getMessage()).header("X-Request-Id", requestId).build();
        }
    }

    @GET
    @Path("/user/{userName}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getUser(@PathParam("userName") String userName) {
        final String requestId = UUID.randomUUID().toString();
        MDC.put("RequestId", requestId);
        try {
            User user = ums.getUser(userName);
            if (null == user) {
                return Response.status(Response.Status.NOT_FOUND).
                        entity("user not found: " + userName).type("text/plain").build();
            }
            return Response.status(Response.Status.OK).entity(user).build();
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
    @Path("/user/{userName}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN + ";charset=utf-8")
    public Response deleteUser(@PathParam("userName") String userName) {
        final String requestId = UUID.randomUUID().toString();
        MDC.put("RequestId", requestId);
        try {
            ums.deleteUser(userName);
            return Response.noContent().build();
        } catch (InfrastructureException ie) {
            logger.error("unable to delete user", ie);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).
                    entity(ie.getMessage()).header("X-Request-Id", requestId).build();
        } catch (RuntimeException re) {
            logger.error("critical error", re);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity(re.getMessage()).header("X-Request-Id", requestId).build();
        }
    }

    @POST
    @Path("/user/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN + ";charset=utf-8")
    public Response logIn(User user) {
        final String requestId = UUID.randomUUID().toString();
        MDC.put("RequestId", requestId);
        try {
            ums.logIn(user);
            return Response.noContent().build();
        } catch (ValidationException ve) {
            logger.error("unable to login", ve);
            return Response.status(Response.Status.BAD_REQUEST).
                    entity(ve.getMessage()).header("X-Request-Id", requestId).build();
        } catch (InfrastructureException ie) {
            logger.error("unable to login", ie);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).
                    entity(ie.getMessage()).header("X-Request-Id", requestId).build();
        } catch (RuntimeException re) {
            logger.error("critical error", re);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity(re.getMessage()).header("X-Request-Id", requestId).build();
        }
    }

    @POST
    @Path("/user/logout/{userName}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN + ";charset=utf-8")
    public Response logOut(@PathParam("userName") String userName) {
        final String requestId = UUID.randomUUID().toString();
        MDC.put("RequestId", requestId);
        try {
            ums.logOut(userName);
            return Response.noContent().build();
        } catch (InfrastructureException ie) {
            logger.error("unable to logout", ie);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).
                    entity(ie.getMessage()).header("X-Request-Id", requestId).build();
        } catch (RuntimeException re) {
            logger.error("critical error", re);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity(re.getMessage()).header("X-Request-Id", requestId).build();
        }
    }

    @PUT
    @Path("/friend/{user1}/{user2}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN + ";charset=utf-8")
    public Response addFriend(@PathParam("user1") String user1,
            @PathParam("user2") String user2) {
        final String requestId = UUID.randomUUID().toString();
        MDC.put("RequestId", requestId);
        try {
            ums.addFriend(user1, user2);
            return Response.status(Response.Status.OK).build();
        } catch (InfrastructureException ie) {
            logger.error("unable to add friend", ie);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).
                    entity(ie.getMessage()).header("X-Request-Id", requestId).build();
        } catch (RuntimeException re) {
            logger.error("critical error", re);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity(re.getMessage()).header("X-Request-Id", requestId).build();
        }
    }

    @GET
    @Path("/friend/list/{userName}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getFriends(@PathParam("userName") String userName) {
        final String requestId = UUID.randomUUID().toString();
        MDC.put("RequestId", requestId);
        try {
            return Response.status(Response.Status.OK).entity(ums.getFriends(userName)).build();
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

    @POST
    @Path("/friend/{user1}/{user2}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN + ";charset=utf-8")
    public Response acceptFriend(@PathParam("user1") String user1,
            @PathParam("user2") String user2) {
        final String requestId = UUID.randomUUID().toString();
        MDC.put("RequestId", requestId);
        try {
            ums.acceptFriend(user1, user2);
            return Response.noContent().build();
        } catch (InfrastructureException ie) {
            logger.error("unable to accept", ie);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).
                    entity(ie.getMessage()).header("X-Request-Id", requestId).build();
        } catch (RuntimeException re) {
            logger.error("critical error", re);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity(re.getMessage()).header("X-Request-Id", requestId).build();
        }
    }

    @GET
    @Path("/friend/accept/{userName}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getAccept(@PathParam("userName") String userName) {
        final String requestId = UUID.randomUUID().toString();
        MDC.put("RequestId", requestId);
        try {
            return Response.status(Response.Status.OK).
                    entity(ums.getAccept(userName)).build();
        } catch (InfrastructureException ie) {
            logger.error("unable to execute query", ie);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).
                    entity(ie.getMessage()).
                    type("text/plain").header("X-Request-Id", requestId).build();
        } catch (RuntimeException re) {
            logger.error("critical error", re);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity(re.getMessage()).
                    type("text/plain").header("X-Request-Id", requestId).build();
        }
    }

    @DELETE
    @Path("/friend/{user1}/{user2}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN + ";charset=utf-8")
    public Response removeFriend(@PathParam("user1") String user1,
            @PathParam("user2") String user2) {
        final String requestId = UUID.randomUUID().toString();
        MDC.put("RequestId", requestId);
        try {
            ums.removeFriend(user1, user2);
            return Response.noContent().build();
        } catch (InfrastructureException ie) {
            logger.error("unable to remove friend", ie);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).
                    entity(ie.getMessage()).header("X-Request-Id", requestId).build();
        } catch (RuntimeException re) {
            logger.error("critical error", re);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity(re.getMessage()).header("X-Request-Id", requestId).build();
        }
    }
}
