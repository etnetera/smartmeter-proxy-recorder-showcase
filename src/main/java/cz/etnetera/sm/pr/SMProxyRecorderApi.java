package cz.etnetera.sm.pr;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SMProxyRecorderApi {

    private String endpoint;

    public SMProxyRecorderApi(String endpoint) {
        this.endpoint = endpoint;
    }

    public void shutdown() throws SMProxyRecorderException {
        try {
            Unirest.shutdown();
        } catch (IOException e) {
            throw new SMProxyRecorderException("Error when shutting down", e);
        }
    }

    public SMProxyRecorderApi status() throws SMProxyRecorderException {
        String status = callEndpoint("status");
        if (!"OK".equals(status)) {
            throw new SMProxyRecorderException("Recorder is not running, got " + status + " status.");
        }
        return this;
    }

    public SMProxyRecorderApi startSubtest(String name, int users, int duration, int rampup) throws SMProxyRecorderException {
        callEndpoint("start-subtest", new HashMap<String, Object>() {{
            put("name", name);
            put("users", users);
            put("duration", duration);
            put("rampup", rampup);
        }});
        return this;
    }

    public SMProxyRecorderApi finishSubtest() throws SMProxyRecorderException {
        callEndpoint("finish-subtest");
        return this;
    }

    public SMProxyRecorderApi setTarget(String name) throws SMProxyRecorderException {
        callEndpoint("set-target", new HashMap<String, Object>() {{
            put("name", name);
        }});
        return this;
    }

    public SMProxyRecorderApi activateDatasource(String datasource, String delimiter, List<String> variables, String domain, Integer port) throws SMProxyRecorderException {
        callEndpoint("activate-datasource", new HashMap<String, Object>() {{
            put("datasource", datasource);
            if (delimiter != null) put("delimiter", delimiter);
            if (variables != null) put("variables", variables.stream().collect(Collectors.joining(",")));
            if (domain != null) put("domain", domain);
            if (port != null) put("port", port);
        }});
        return this;
    }

    public SMProxyRecorderApi activateCSV(String filename, String delimiter, List<String> variables) throws SMProxyRecorderException {
        callEndpoint("activate-csv", new HashMap<String, Object>() {{
            put("filename", filename);
            if (delimiter != null) put("delimiter", delimiter);
            if (variables != null) put("variables", variables.stream().collect(Collectors.joining(",")));
        }});
        return this;
    }

    public SMProxyRecorderApi addReplacer(String key) throws SMProxyRecorderException {
        return addReplacers(new HashMap<String, String>() {{ put(key, key); }});
    }

    public SMProxyRecorderApi addReplacer(String key, String placeholder) throws SMProxyRecorderException {
        return addReplacers(new HashMap<String, String>() {{ put(key, placeholder); }});
    }

    public SMProxyRecorderApi addReplacers(String... replacers) throws SMProxyRecorderException {
        return addReplacers(Arrays.asList(replacers));
    }

    public SMProxyRecorderApi addReplacers(List<String> replacers) throws SMProxyRecorderException {
        return addReplacers(replacers.stream().collect(Collectors.toMap(replacer -> replacer, replacer -> replacer)));
    }

    public SMProxyRecorderApi addReplacers(Map<String, String> replacers) throws SMProxyRecorderException {
        callEndpoint("add-replacers",
                request -> replacers.entrySet().forEach(
                        replacer -> request.queryString("replacer", replacer.getKey() + "~" + replacer.getValue())));
        return this;
    }

    public SMProxyRecorderApi removeReplacer(String key) throws SMProxyRecorderException {
        return removeReplacers(Collections.singletonList(key));
    }

    public SMProxyRecorderApi removeReplacers(String... keys) throws SMProxyRecorderException {
        return removeReplacers(Arrays.asList(keys));
    }

    public SMProxyRecorderApi removeReplacers(List<String> keys) throws SMProxyRecorderException {
        callEndpoint("remove-replacers", request -> keys.forEach(key -> request.queryString("replacer", key)));
        return this;
    }

    public SMProxyRecorderApi addVariable(String name, String value) throws SMProxyRecorderException {
        return addVariables(new HashMap<String, String>() {{ put(name, value); }});
    }

    public SMProxyRecorderApi addVariables(Map<String, String> variables) throws SMProxyRecorderException {
        callEndpoint("add-variable",
                request -> variables.entrySet().forEach(
                        variable -> request.queryString("variable", variable.getKey() + "~" + variable.getValue())));
        return this;
    }

    public SMProxyRecorderApi insertPause() throws SMProxyRecorderException {
        return insertPause(null);
    }

    public SMProxyRecorderApi insertPause(Long duration) throws SMProxyRecorderException {
        callEndpoint("insert-pause", new HashMap<String, Object>() {{
            if (duration != null) put("duration", duration);
        }});
        return this;
    }

    public SMProxyRecorderApi exportTest(String testName) throws SMProxyRecorderException {
        callEndpoint("export-test", new HashMap<String, Object>() {{
            if (testName != null) put("testName", testName);
        }});
        return this;
    }

    public SMProxyRecorderApi clearRecording() throws SMProxyRecorderException {
        callEndpoint("clear-recording");
        return this;
    }

    public SMProxyRecorderApi runTest(String testName, Boolean gui, String monitorName) throws SMProxyRecorderException {
        callEndpoint("run-test", new HashMap<String, Object>() {{
            put("testName", testName);
            if (gui != null) put("gui", gui);
            if (monitorName != null) put("monitorName", monitorName);
        }});
        return this;
    }

    private String callEndpoint(String command) throws SMProxyRecorderException {
        return callEndpoint(command, (Consumer<GetRequest>) null);
    }

    private String callEndpoint(String command, Map<String, Object> fields) throws SMProxyRecorderException {
        return callEndpoint(command, request -> request.queryString(fields));
    }

    private String callEndpoint(String command, Consumer<GetRequest> unirestModifier) throws SMProxyRecorderException {
        try {
            GetRequest request = Unirest.get(endpoint + "/proxy-driver");
            request.queryString("command", command);
            if (unirestModifier != null) unirestModifier.accept(request);
            HttpResponse<String> response = request.asString();
            if (response.getStatus() != 200) {
                throw new SMProxyRecorderException("Wrong http status code " + response.getStatus() + " when calling command " + command + " on endpoint " + endpoint);
            }
            return response.getBody();
        } catch (UnirestException e) {
            throw new SMProxyRecorderException("Error when calling command " + command + " on endpoint " + endpoint, e);
        }
    }

    public static class SMProxyRecorderException extends Exception {

        private SMProxyRecorderException(String s) {
            super(s);
        }

        private SMProxyRecorderException(String s, Throwable e) {
            super(s, e);
        }

    }

}
