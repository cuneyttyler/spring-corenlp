//package com.gsu.knowledgebase.controller.OldSaveEntity;
//
//import com.gsu.common.util.ImageUtils;
//import com.gsu.knowledgebase.model.*;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import java.awt.image.BufferedImage;
//import java.util.*;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
///**
// * Created by cnytync on 14/02/2018.
// */
//public class java {
//
//    @RequestMapping(value = "/saveEntity", method = RequestMethod.POST,
//            consumes = MediaType.APPLICATION_JSON_VALUE,
//            produces = MediaType.APPLICATION_JSON_VALUE)
//    public
//    @ResponseBody
//    Object saveEntity(@RequestBody final Entity entity) throws Exception {
//        Entity pl = knowledgeBaseDao.findEntityByName(entity.getName());
//        if (pl != null && !pl.getEntityType().equals("web-page-annotation")) {
//            return null;
//        }
//
//        if (entity.getEntityType().equals("web-page")) {
//            try {
//                String title = webPageAnnotator.getWebPageTitle(entity.getWebUri());
//                if (title == null || title.isEmpty())
//                    entity.setName(entity.getWebUri());
//                else
//                    entity.setName(title);
//
//                entity.setImage(webPageAnnotator.getWebsiteImage(entity.getWebUri()));
//                entity.setWebPageText(webPageAnnotator.getWebPageTextAsHtml(entity.getWebUri()));
//            } catch (Exception e) {
//                entity.setName(entity.getWebUri());
//                e.printStackTrace();
//            }
//        }
//
//        // commons.wikimedia redirect problem workaround
//        if (entity.getImage() != null && entity.getImage().contains("commons.wikimedia")) {
//            entity.setImage(entity.getSecondaryImage());
//        }
//
//        if (entity.getImage() != null && !entity.getImage().isEmpty()) {
//            BufferedImage bf = imageUtils.readImageFromUri(entity.getImage());
//
//            if (bf != null) {
//                String filename = imageUtils.saveJpegPngImage(bf, entity.getName().replace(" ", "_") + "_" + entity.getWikidataId());
//                entity.setImage(filename);
//
//                BufferedImage scaledImage = imageUtils.getScaledImage(bf, ImageUtils.SCALED_IMAGE_WIDTH, ImageUtils.SCALED_IMAGE_HEIGHT);
//                String smallFilename = imageUtils.saveScaledJpegPngImage(scaledImage, entity.getName() + "_" + entity.getWikidataId());
//                entity.setSmallImage(smallFilename);
//            }
//        }
//
//        if (entity.getSecondaryImage() != null && !entity.getSecondaryImage().isEmpty()) {
//            BufferedImage bf = imageUtils.readImageFromUri(entity.getSecondaryImage());
//
//            if (bf != null) {
//                String filename = imageUtils.saveJpegPngImage(bf, entity.getName().replace(" ", "_") + "_" + entity.getWikidataId() + "_secondary");
//                entity.setSecondaryImage(filename);
//
//                BufferedImage scaledImage = imageUtils.getScaledImage(bf, ImageUtils.SCALED_IMAGE_WIDTH, ImageUtils.SCALED_IMAGE_HEIGHT);
//                String smallFilename = imageUtils.saveScaledJpegPngImage(scaledImage, entity.getName() + "_" + entity.getWikidataId() + "_secondary");
//                entity.setSmallSecondaryImage(smallFilename);
//            }
//        }
//
//
//
//        final Long id = knowledgeBaseDao.saveEntity(entity);
//        entity.setId(id);
//
//        final ExecutorService executor = Executors.newSingleThreadExecutor();
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                if (entity.getEntityType().equals("web-page")) {
//                    List<String> uris = new ArrayList<String>();
//                    try {
//                        uris = webPageAnnotator.annotateWebPage(entity.getWebUri());
//                    } catch (Exception e) {
//                        System.err.println("Error at annotating web page");
//                        e.printStackTrace();
//                    }
//
//                    Map<String, Entity> webPageEntities = new HashMap<>();
//                    for (String dbpediaUri : uris) {
//                        try {
//                            if (webPageEntities.get(dbpediaUri) == null) {
//                                Entity webPageEntity = dbPedia.getEntityByUri(dbpediaUri);
//                                webPageEntity.setWebPageEntityId(id);
//
//                                webPageEntities.put(webPageEntity.getDbpediaUri(), webPageEntity);
//                            } else {
//                                // do nothing...
//                            }
//
//                        } catch (Exception e) {
//                            System.err.println("Error at dbPedia.getEntityByUri");
//                            e.printStackTrace();
//                        }
//                    }
//
//                    for (String key : webPageEntities.keySet()) {
//                        Entity webPageEntity = webPageEntities.get(key);
//
//                        try {
//                            Long webPageEntityId = knowledgeBaseDao.saveEntity(webPageEntity);
//
//                            for (Property property : webPageEntity.getProperties()) {
//                                property.setEntityId(webPageEntityId);
//                            }
//
//
//                            // save to metaproperty if not exists before
//                            // fetch from metaproperty
//                            // if not exists there, save this prop to metaproperty first
//                            for (Property p : webPageEntity.getProperties()) {
//                                MetaProperty mp = knowledgeBaseDao.findMetapropertyByUri(p.getUri());
//                                if (mp == null) {
//                                    Long mpId = knowledgeBaseDao.saveMetaProperty(p);
//                                    p.setMetaPropertyId(mpId);
//                                } else {
//                                    p.setMetaPropertyId(mp.getId());
//                                }
//                            }
//
//                            knowledgeBaseDao.saveProperties(webPageEntity.getProperties());
//
//                            List<Subproperty> subproperties = new ArrayList<>();
//                            for (Property property : webPageEntity.getProperties()) {
//                                for (Subproperty subproperty : property.getSubproperties()) {
//                                    subproperty.setPropertyId(property.getId());
//                                    subproperties.add(subproperty);
//                                }
//                            }
//
//                            knowledgeBaseDao.saveSubproperties(subproperties);
//                        } catch (Exception e) {
//                            System.err.println("Error at saving web page entity.");
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//        });
//
//        for (Property property : entity.getProperties()) {
//            property.setEntityId(id);
//
//            MetaProperty mp = knowledgeBaseDao.findMetapropertyByUri(property.getUri());
//            if (mp == null) {
//                Long mpId = knowledgeBaseDao.saveMetaProperty(property);
//                property.setMetaPropertyId(mpId);
//            } else {
//                property.setMetaPropertyId(mp.getId());
//            }
//
//            if (property.getValueLabel() != null && property.getValueLabel().isEmpty()) {
//                property.setValueLabel(property.getValue());
//            }
//        }
//
//        // save to metaproperty if not exists before
//        knowledgeBaseDao.saveProperties(entity.getProperties());
//
//        List<Subproperty> subproperties = new ArrayList<>();
//        for (Property property : entity.getProperties()) {
//            for (Subproperty subproperty : property.getSubproperties()) {
//                subproperty.setPropertyId(property.getId());
//                subproperties.add(subproperty);
//            }
//        }
//
//        knowledgeBaseDao.saveSubproperties(subproperties);
//
//        final ExecutorService executor2 = Executors.newSingleThreadExecutor();
//        executor2.execute(new Runnable() {
//            @Override
//            public void run() {
//                Collection<AnnotationItem> items = dbpediaSpotlight.annotateText(entity.getDescription());
//
//                for (AnnotationItem i : items) {
//                    i.setReferencedEntityId(entity.getId());
//                }
//
//                try {
//                    knowledgeBaseDao.saveAnnotationItems(new ArrayList<AnnotationItem>(items));
//                } catch (Exception e) {
//                    System.err.println("ERROR at dbpediaspotlight, saving annotation items...");
//                    e.printStackTrace();
//                }
//            }
//        });
//
//
//        return entity;
//    }
//}
