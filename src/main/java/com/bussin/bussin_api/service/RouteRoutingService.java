package com.bussin.bussin_api.service;

import java.math.*;
import java.net.*;
import java.net.http.*;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bussin.bussin_api.dto.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RouteRoutingService {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String routingBaseUrl;

    public RouteRoutingService(ObjectMapper objectMapper,
            @Value("${bussin.routing.base-url:https://router.project-osrm.org}") String routingBaseUrl) {
        this.objectMapper=objectMapper;
        this.routingBaseUrl=routingBaseUrl.replaceAll("/+$","");
        this.httpClient=HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    }

    public RoutePreviewResponse preview(RoutePreviewRequest request) {
        String coordinates=request.getOriginLongitude()+","+request.getOriginLatitude()+";"+
                request.getDestinationLongitude()+","+request.getDestinationLatitude();
        String url=routingBaseUrl+"/route/v1/driving/"+coordinates+"?overview=full&geometries=geojson&steps=false";
        try {
            HttpRequest httpRequest=HttpRequest.newBuilder().uri(URI.create(url)).timeout(Duration.ofSeconds(20))
                    .header("Accept","application/json").GET().build();
            HttpResponse<String> response=httpClient.send(httpRequest,HttpResponse.BodyHandlers.ofString());
            if(response.statusCode()/100!=2) throw new IllegalStateException("Routing provider returned HTTP "+response.statusCode());
            JsonNode root=objectMapper.readTree(response.body());
            if(!"Ok".equals(root.path("code").asText()) || root.path("routes").isEmpty())
                throw new IllegalStateException(root.path("message").asText("No drivable route was found."));
            JsonNode route=root.path("routes").get(0);
            BigDecimal distanceKm=BigDecimal.valueOf(route.path("distance").asDouble()/1000.0).setScale(2,RoundingMode.HALF_UP);
            int durationMinutes=(int)Math.ceil(route.path("duration").asDouble()/60.0);
            return new RoutePreviewResponse(distanceKm,durationMinutes,objectMapper.writeValueAsString(route.path("geometry")));
        } catch(Exception e) {
            throw new IllegalStateException("Unable to calculate the road route: "+e.getMessage(),e);
        }
    }
}