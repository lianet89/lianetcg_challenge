package com.example.LianetCG_challenge.dto;

import com.example.LianetCG_challenge.config.Kind;
import com.fasterxml.jackson.databind.JsonNode;

public record SinkADto (Kind kind, JsonNode id){}
