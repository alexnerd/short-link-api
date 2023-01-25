/*
 * Copyright 2023 Aleksey Popov <alexnerd.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.alexnerd.shortlink.buisness.boundary;

import com.alexnerd.shortlink.buisness.control.ShortLinkException;
import com.alexnerd.shortlink.buisness.control.ShortLinkStore;
import com.alexnerd.shortlink.buisness.entity.ShortLink;
import org.eclipse.microprofile.metrics.annotation.Metered;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

@Path("/link")
public class ShortLinksResource {

    @Inject
    ShortLinkStore store;

    @POST
    @Metered
    @Path("/short-link")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getShortLink(@Valid @NotNull ShortLink shortLink, @Context UriInfo info) {
        if (shortLink.shortLink != null) {
            throw new ShortLinkException("Short link was invalidly set on request", 422);
        }
        shortLink = store.getShortLink(shortLink);
        URI location = info.getBaseUriBuilder()
                .path("link/" + shortLink.shortLink)
                .build();
        return Response.created(location).build();
    }

    @GET
    @Metered
    @Path("/{shortLink}")
    @Produces(MediaType.TEXT_HTML)
    public Response redirect(@PathParam("shortLink") String shortLink, @Context UriInfo info) {
        return Response.seeOther(URI.create(store.getRedirectLink(shortLink))).build();
    }
}