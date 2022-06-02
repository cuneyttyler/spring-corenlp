package com.gsu.semantic.controller;

import com.gsu.common.util.ImageUtils;
import com.gsu.semantic.model.Place;
import com.gsu.semantic.model.Property;
import com.gsu.semantic.repository.HistoricalIstanbulDao;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.Collection;

/**
 * Created by cnytync on 21/12/14.
 */
@Controller("HistoricalIstanbulApi")
@RequestMapping(value = "/historicalIstanbul/api")
public class HistoricalIstanbulApi {

    @Autowired
    private HistoricalIstanbulDao historicalIstanbulDao;

    @ResponseBody
    @RequestMapping(value = "/image/{image:.*}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getImage(@PathVariable("image") String image) throws IOException {
        if (image == null || image.isEmpty()) {
            return null;
        }

        File f = new File(ImageUtils.HI_IMG_LOCATION + image);
        try {
            InputStream fis = new FileInputStream(f);
            byte[] bytes = IOUtils.toByteArray(fis);
            fis.close();

            return bytes;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @RequestMapping(value = "/savePlace", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object savePlace(@RequestBody Place place) throws Exception {
        Place pl = historicalIstanbulDao.findPlaceByName(place.getName());
        if (pl != null) {
            return null;
        }

        Long id = historicalIstanbulDao.savePlace(place);

        for (Property property : place.getProperties()) {
            property.setPlaceId(id);

            historicalIstanbulDao.saveProperty(property);
        }

        return id;
    }

    @RequestMapping(value = "/getAllPlaces", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object getAllPlaces() throws Exception {
        Collection<Place> places = historicalIstanbulDao.findAllPlaces();

        return places;
    }
}
