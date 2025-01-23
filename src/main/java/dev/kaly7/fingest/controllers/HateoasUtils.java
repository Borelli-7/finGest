package dev.kaly7.fingest.controllers;

import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public class HateoasUtils {

    public static URI relativeHref(Object invocationValue) {
        // Generate the absolute URI using WebMvcLinkBuilder
        URI absoluteUri = WebMvcLinkBuilder.linkTo(invocationValue).toUri();

        // Build a relative URI using UriComponentsBuilder
        return UriComponentsBuilder.newInstance()
                .path(absoluteUri.getPath())
                .query(absoluteUri.getQuery())
                .fragment(absoluteUri.getFragment())
                .build()
                .toUri();
    }
}
