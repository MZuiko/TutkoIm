package hu.elte.szakdolgozat.main;

import hu.elte.szakdolgozat.db.MysqlService;
import hu.elte.szakdolgozat.rest.server.RestServer;
import hu.elte.szakdolgozat.services.InfrastructureException;
import java.io.IOException;

public class TutkoIm {

    public static void main(String[] args) throws IOException, InfrastructureException {
        RestServer.main(args);
        MysqlService.getInstance().close();
    }
}
