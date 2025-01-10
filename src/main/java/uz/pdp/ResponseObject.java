package uz.pdp;

import com.google.gson.Gson;

import java.util.List;

public class ResponseObject {

    public static class Candidate {
        public Content content;
        public String finishReason;
        public CitationMetadata citationMetadata;
        public double avgLogprobs;

    }

    public static class Content {
        public List<Part> parts;
        public String role;
    }

    public static class Part {
        public String text;
    }

    public static class CitationMetadata {
        public List<CitationSource> citationSources;
    }

    public static class CitationSource {
        public int startIndex;
        public int endIndex;
        public String uri;
    }

    public List<Candidate> candidates;
    public UsageMetadata  usageMetadata;
    public String modelVersion;

    public static ResponseObject fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, ResponseObject.class);
    }
}