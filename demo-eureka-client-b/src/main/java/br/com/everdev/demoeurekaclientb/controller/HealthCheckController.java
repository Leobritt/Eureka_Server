package br.com.everdev.demoeurekaclientb.controller;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Applications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@RestController
public class HealthCheckController {

    @Autowired
    @Lazy
    private EurekaClient eurekaClient;

    @Value("${spring.application.name}")
    private String appName;

    @GetMapping("/health")
    public String healthy() {
        return "Estpu vivo e bem! Sou a app " + appName + " - " + LocalDateTime.now();
    }

    @GetMapping("/discover")
    public String discover() {
        Applications otherApps = eurekaClient.getApplications();
        return otherApps.getRegisteredApplications().toString();
    }

    @PostMapping("/receiveCall/{name}")
    public String receiveCall(@PathVariable String name, @RequestBody String message) {
        return message + "\nOlá " + name + ". Aqui é " + appName + " e recebi sua mensagem.";
    }

    @GetMapping("/makeCall/{name}")
    public String makeCall(@PathVariable String name) throws URISyntaxException {
        String message = "Olá, tem alguem ai??";

        List<InstanceInfo> instances = eurekaClient.getInstancesById(name);

        Optional<InstanceInfo> instance = instances.stream().findFirst();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://" + instance.get().getIPAddr() + ":" + instance.get().getPort() + "/receiveCall/"
                        + appName))
                .POST(HttpRequest.BodyPublishers.ofString(message))
                .build();
        try {
            HttpResponse<String> response = HttpClient.newBuilder().build().send(request,
                    HttpResponse.BodyHandlers.ofString());
            return response.body().toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/number-generate/{nameC}")
    public Integer generate(@PathVariable String nameC) throws URISyntaxException {
        Random random = new Random();
        int number_generate_from_b = random.nextInt(100);

        List<InstanceInfo> instances = eurekaClient.getInstancesById(nameC);

        InstanceInfo instance = instances.getFirst();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://" + instance.getIPAddr() + ":" + instance.getPort() + "/random-number/" + nameC))
                .GET()
                .build();

        try {
            HttpResponse<String> response = HttpClient.newBuilder().build().send(request,
                    HttpResponse.BodyHandlers.ofString());

            int number_generate_from_c;
            number_generate_from_c = Integer.parseInt(response.body());
            return number_generate_from_b + number_generate_from_c;

        } catch (Exception e) {
            // TODO: handle exception
            return -1;

        }

    }

}
