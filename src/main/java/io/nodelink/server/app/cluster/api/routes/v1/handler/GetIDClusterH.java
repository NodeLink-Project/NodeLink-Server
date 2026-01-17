package io.nodelink.server.app.cluster.api.routes.v1.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.javalin.http.Context;
import io.nodelink.server.app.infra.ApiHandler;

import java.util.Random;

public class GetIDClusterH implements ApiHandler {
    private final Random random = new Random();

    @Override
    public void handle(Context ctx) throws Exception {
        int idNumber = random.nextInt(10000);

        ObjectNode response = new ObjectMapper().createObjectNode();
        response.put("id", idNumber);

        ctx.json(response);
    }
}
