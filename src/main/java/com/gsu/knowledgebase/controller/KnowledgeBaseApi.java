package com.gsu.knowledgebase.controller;

import com.gsu.common.util.DateUtils;
import com.gsu.common.util.ImageUtils;
import com.gsu.knowledgebase.model.*;
import com.gsu.knowledgebase.repository.KnowledgeBaseDao;
import com.gsu.knowledgebase.service.DBPedia;
import com.gsu.knowledgebase.service.DBpediaSpotlight;
import com.gsu.knowledgebase.service.MailService;
import com.gsu.knowledgebase.service.WebPageAnnotator;
import com.gsu.knowledgebase.util.Constants;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller("KnowledgeBaseApi")
@RequestMapping(value = "/knowledgeBase/api")
public class KnowledgeBaseApi {

    @Autowired
    private KnowledgeBaseDao knowledgeBaseDao;

    @Autowired
    private WebPageAnnotator webPageAnnotator;

    @Autowired
    private DBPedia dbPedia;

    @Autowired
    private ImageUtils imageUtils;

    @Autowired
    private DBpediaSpotlight dbpediaSpotlight;

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private MailService mailService;

    @RequestMapping(value = "/signup", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object signup(@RequestBody final User user) throws Exception {
        User u = knowledgeBaseDao.findUserByUsername(user.getUsername());
        if (u != null) {
            return -1;
        }

        u = knowledgeBaseDao.findUserByEmail(user.getEmail());
        if (u != null) {
            return -2;
        }

        user.setRoleId(new Long(Constants.ROLE_USER));
        user.setStatus(Constants.USER_STATUS_AWAITING_CONFIRMATION);

        // Save User
        Long userId = knowledgeBaseDao.saveUser(user);

        // Create confirmation code and save
        UUID uuid = UUID.randomUUID();

        UserConfirmation uc = new UserConfirmation();
        uc.setStatus(Constants.USER_CONFIRMATION_STATUS_AWAITING);
        uc.setUserId(userId);
        uc.setConfirmationCode(uuid.toString());

        knowledgeBaseDao.saveUserConfirmation(uc);

        // Send confirmation mail
        Mail mail = new Mail();
        mail.setMailTo(user.getEmail());
        mail.setMailSubject("Confirmation Email");

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("firstName", user.getFirstName());
        model.put("lastName", user.getLastName());
        model.put("confirmationCode", uc.getConfirmationCode());

        mail.setModel(model);

        mailService.sendEmail(mail, "/templates/confirmation.vm");

        return user;
    }

    @RequestMapping(value = "/setUserEntity/", method = RequestMethod.GET)
    public void setUserEntity() throws Exception {
//        Collection<Entity> entities = knowledgeBaseDao.findAllEntitiesLazy();
//
//        for (Entity e : entities) {
//            UserEntity ue = new UserEntity();
//            ue.setUserId(1l);
//            ue.setCrDate(DateUtils.getDateNowDateTime());
//            ue.setEntityId(e.getId());
//            ue.setNote(e.getNote());
//
//            Long id = knowledgeBaseDao.saveUserEntity(ue);
//            if (e.getCategoryId() != null && e.getCategoryId() != 0)
//                knowledgeBaseDao.saveUserEntityCategory(id, e.getCategoryId());
//            if (e.getSubCategoryId() != null && e.getSubCategoryId() != 0)
//                knowledgeBaseDao.saveUserEntitySubcategory(id, e.getSubCategoryId());
//        }
    }

    @RequestMapping(value = "/confirmUser/{confirmationCode}", method = RequestMethod.GET)
    public String confirmUser(@PathVariable String confirmationCode) throws Exception {
        UserConfirmation uc = knowledgeBaseDao.findAwaitingUserConfirmationByCode(confirmationCode);

        if (uc == null) {
            return "redirect:/knowledge-base/confirmation-code-not-found";
        }

        // Update user and confirmation status
        knowledgeBaseDao.updateUserStatus(uc.getUserId(), Constants.USER_STATUS_CONFIRMED);
        knowledgeBaseDao.updateUserConfirmationStatus(uc.getId(), Constants.USER_CONFIRMATION_STATUS_CONFIRMED);

        User user = knowledgeBaseDao.findUserById(uc.getUserId());

        // Send registration mail
        Mail mail = new Mail();
        mail.setMailTo(user.getEmail());
        mail.setMailSubject("Registration Completed");

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("firstName", user.getFirstName());
        model.put("lastName", user.getLastName());

        mail.setModel(model);

        mailService.sendEmail(mail, "/templates/registration.vm");

        return "redirect:/knowledge-base/user-confirmed";
    }

    @ResponseBody
    @RequestMapping(value = "/image/{image:.*}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getImage(@PathVariable("image") String image) throws IOException {
        if (image == null || image.isEmpty()) {
            return null;
        }

        File f = new File(ImageUtils.KB_IMG_LOCATION + image);
        try {
            InputStream fis = new FileInputStream(f);
            byte[] bytes = IOUtils.toByteArray(fis);
            fis.close();

            return bytes;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @RequestMapping(value = "/updateEntity", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object updateEntity(@RequestBody Entity entity) throws Exception {
        knowledgeBaseDao.updateEntity(entity);
        knowledgeBaseDao.updateUserEntity(new UserEntity(entity));

        return entity.getId();
    }

    @RequestMapping(value = "/unexpectedError", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object unexpectedError(@RequestBody UnexpectedError error) throws Exception {
        // TODO save unexpected error log

        return 0;
    }

    @RequestMapping(value = "/saveCustomProperty", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object saveCustomProperty(@RequestBody CustomProperty customProperty) throws Exception {
        Entity s = customProperty.getSubject();
        MetaProperty mp = customProperty.getPredicate();
        Entity o = customProperty.getObject();

        if (mp.getId() == null) {
            mp.setVisibility(3);
            Long id = knowledgeBaseDao.saveMetaProperty(mp);
            mp.setId(id);
        }

        if (o.getId() == null) {
            Entity e = knowledgeBaseDao.findEntityByDbpediaUriWikidataId(o);

            if (e == null) {
                o.setSource("custom");
                Long id = knowledgeBaseDao.saveEntity(o);

                o.setId(id);
            } else {
                customProperty.setObject(e);
            }
        }

        Property p = new Property(mp);
        p.setSource("custom");

        if (o.getEntityType().equals("semantic-web")) {
            if (o.getDbpediaUri() != null && o.getDbpediaUri().length() > 0) {
                p.setValue(o.getDbpediaUri());
            } else {
                p.setValue(o.getWikidataId());
            }
        } else { // custom entity
            p.setValue(o.getName());
            p.setCustomEntityId(o.getId());
        }

        p.setEntityId(customProperty.getSubject().getId());
        p.setValueLabel(o.getName());

        // if such a triple does not exists then save...
        List<Property> results = knowledgeBaseDao.findPropertiesByTriples(s.getId(), p.getMetaPropertyId(), p.getValueLabel());
        if (results == null || results.size() == 0) {
            Long id = knowledgeBaseDao.saveProperty(p);
            p.setId(id);
        } else {
            p.setId(results.get(0).getId());
        }

        return p;
    }

    @RequestMapping(value = "/saveCustomProperties", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    void saveCustomProperties(@RequestBody CustomProperty[] customProperties) throws Exception {
        for (CustomProperty customProperty : customProperties) {
            Entity s = customProperty.getSubject();
            MetaProperty mp = customProperty.getPredicate();
            Entity o = customProperty.getObject();

            if (mp.getId() == null) {
                mp.setVisibility(3);
                Long id = knowledgeBaseDao.saveMetaProperty(mp);
                mp.setId(id);
            }

            if (o.getId() == null) {
                Entity e = knowledgeBaseDao.findEntityByDbpediaUriWikidataId(o);

                if (e == null) {
                    o.setSource("custom");
                    Long id = knowledgeBaseDao.saveEntity(o);

                    o.setId(id);
                } else {
                    customProperty.setObject(e);
                }
            }

            Property p = new Property(mp);
            p.setSource("custom");

            if (o.getEntityType().equals("semantic-web")) {
                if (o.getDbpediaUri() != null && o.getDbpediaUri().length() > 0) {
                    p.setValue(o.getDbpediaUri());
                } else {
                    p.setValue(o.getWikidataId());
                }
            } else { // custom entity
                p.setValue(o.getName());
                p.setCustomEntityId(o.getId());
            }

            p.setEntityId(customProperty.getSubject().getId());
            p.setValueLabel(o.getName());

            // if such a triple does not exists then save...
            List<Property> results = knowledgeBaseDao.findPropertiesByTriples(s.getId(), p.getMetaPropertyId(), p.getValueLabel());
            if (results == null || results.size() == 0) {
                Long id = knowledgeBaseDao.saveProperty(p);
                p.setId(id);
            } else {
                p.setId(results.get(0).getId());
            }
        }


    }

    @RequestMapping(value = "/removeEntity/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object removeEntity(@PathVariable Long id) throws Exception {
        knowledgeBaseDao.removeEntity(id);

        return id;
    }

    @RequestMapping(value = "/saveEntity", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object saveUserEntity(@RequestBody final Entity entity, HttpServletRequest request) throws Exception {
        Long userId = (Long) request.getSession().getAttribute("userId");

        Entity e = knowledgeBaseDao.findEntityByName(entity.getName());
        UserEntity ue = null;
        if (e != null)
            ue = knowledgeBaseDao.findUserEntity(userId, e.getId());

        // If user entity exist
        if (ue != null && !e.getEntityType().equals("web-page-annotation")) {
            return -1;
        }

        if (e == null)
            e = saveEntity(entity);

        ue = new UserEntity();
        ue.setUserId(userId);
        ue.setEntityId(e.getId());
        ue.setCrDate(DateUtils.now());
        ue.setNote(entity.getNote());

        Long id = knowledgeBaseDao.saveUserEntity(ue);
        if (entity.getTopics().size() > 0)
            knowledgeBaseDao.addEntityToTopic(id, entity.getTopics().get(0).getId());
        if (entity.getCategories().size() > 0)
            knowledgeBaseDao.addEntityToCategory(id, entity.getCategories().get(0).getId());
        if (entity.getSubCategories().size() > 0)
            knowledgeBaseDao.addEntityToSubCategory(id, entity.getSubCategories().get(0).getId());

        return entity;
    }

    public Entity saveEntity(final Entity entity) throws Exception {

        if (entity.getEntityType().equals("web-page")) {
            try {
                entity.setWebPageText(webPageAnnotator.getWebPageTextAsHtml(entity.getWebUri()));
                entity.setDescription(entity.getWebPageText());

                String title = webPageAnnotator.getWebPageTitle(entity.getWebUri());
                if (title == null || title.isEmpty())
                    entity.setName(entity.getWebUri());
                else
                    entity.setName(title);

                entity.setImage(webPageAnnotator.getWebsiteImage(entity.getWebUri()));
            } catch (Exception e) {
                entity.setName(entity.getWebUri());
                e.printStackTrace();
            }
        }

        // commons.wikimedia redirect problem workaround
        if (entity.getImage() != null && entity.getImage().contains("commons.wikimedia")) {
            entity.setImage(entity.getSecondaryImage());
        }

        if (entity.getImage() != null && !entity.getImage().isEmpty()) {
            BufferedImage bf = imageUtils.readImageFromUri(entity.getImage());

            if (bf != null) {
                String filename = imageUtils.saveJpegPngImage(bf, entity.getName().replace(" ", "_") + "_" + entity.getWikidataId());
                entity.setImage(filename);

                BufferedImage scaledImage = imageUtils.getScaledImage(bf, ImageUtils.SCALED_IMAGE_WIDTH, ImageUtils.SCALED_IMAGE_HEIGHT);
                String smallFilename = imageUtils.saveScaledJpegPngImage(scaledImage, entity.getName() + "_" + entity.getWikidataId());
                entity.setSmallImage(smallFilename);
            }
        }

        final ExecutorService executor3 = Executors.newSingleThreadExecutor();
        executor3.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (entity.getSecondaryImage() != null && !entity.getSecondaryImage().isEmpty()) {
                        BufferedImage bf = imageUtils.readImageFromUri(entity.getSecondaryImage());

                        if (bf != null) {
                            String filename = imageUtils.saveJpegPngImage(bf, entity.getName().replace(" ", "_") + "_" + entity.getWikidataId() + "_secondary");
                            entity.setSecondaryImage(filename);

                            BufferedImage scaledImage = imageUtils.getScaledImage(bf, ImageUtils.SCALED_IMAGE_WIDTH, ImageUtils.SCALED_IMAGE_HEIGHT);
                            String smallFilename = imageUtils.saveScaledJpegPngImage(scaledImage, entity.getName() + "_" + entity.getWikidataId() + "_secondary");
                            entity.setSmallSecondaryImage(smallFilename);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        final Long id = knowledgeBaseDao.saveEntity(entity);
        entity.setId(id);

        for (Property property : entity.getProperties()) {
            property.setEntityId(id);

            MetaProperty mp = knowledgeBaseDao.findMetapropertyByUri(property.getUri());
            if (mp == null) {
                Long mpId = knowledgeBaseDao.saveMetaProperty(property);
                property.setMetaPropertyId(mpId);
            } else {
                property.setMetaPropertyId(mp.getId());
            }

            if (property.getValueLabel() != null && property.getValueLabel().isEmpty()) {
                property.setValueLabel(property.getValue());
            }
        }

        // save to metaproperty if not exists before
        knowledgeBaseDao.saveProperties(entity.getProperties());

        List<Subproperty> subproperties = new ArrayList<>();
        for (Property property : entity.getProperties()) {
            for (Subproperty subproperty : property.getSubproperties()) {
                subproperty.setPropertyId(property.getId());
                subproperties.add(subproperty);
            }
        }

        knowledgeBaseDao.saveSubproperties(subproperties);

        final ExecutorService executor2 = Executors.newSingleThreadExecutor();
        executor2.execute(new Runnable() {
            @Override
            public void run() {
                Collection<AnnotationItem> items = dbpediaSpotlight.annotateText(entity.getDescription());

                for (AnnotationItem i : items) {
                    i.setReferencedEntityId(entity.getId());
                }

                try {
                    knowledgeBaseDao.saveAnnotationItems(new ArrayList<AnnotationItem>(items));
                } catch (Exception e) {
                    System.err.println("ERROR at dbpediaspotlight, saving annotation items...");
                    e.printStackTrace();
                }
            }
        });

        return entity;
    }

    @RequestMapping(value = "/transferMetaprops", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object transferMetaprops() throws Exception {
        Collection<MetaProperty> metaProperties = knowledgeBaseDao.findAllMetaproperties();

        Collection<Property> properties = knowledgeBaseDao.findAllProperties();

        List<Property> propsToTransfer = new ArrayList<>();
        for (Property p : properties) {

            boolean exists = false;
            for (MetaProperty mp : metaProperties) {
                if (mp.getUri() != null && mp.getUri().equals(p.getUri())) {
                    exists = true;
                }
            }

            for (Property mp : propsToTransfer) {
                if (mp.getUri() != null && mp.getUri().equals(p.getUri())) {
                    exists = true;
                }
            }

            if (!exists && p.getUri() != null) {
                propsToTransfer.add(p);
            }
        }

        knowledgeBaseDao.transferPropsToMetaProps(propsToTransfer);

        return "Successful";
    }

    @RequestMapping(value = "/matchPropsToMetaprops", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object matchPropsToMetaprops() throws Exception {
        Collection<MetaProperty> metaProperties = knowledgeBaseDao.findAllMetaproperties();

        List<Property> properties = knowledgeBaseDao.findAllProperties();

        for (Property p : properties) {
            for (MetaProperty mp : metaProperties) {
                if (p.getUri() != null && mp.getUri() != null && p.getUri().equals(mp.getUri())) {
                    p.setMetaPropertyId(mp.getId());
                }
            }
        }

        knowledgeBaseDao.updateProperties(properties);

        return "Successful";
    }

    @RequestMapping(value = "/getAllEntities", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object getAllEntities() throws Exception {
        Collection<Entity> entities = knowledgeBaseDao.findAllEntities();

        for (Entity e : entities) {
            if (e.getEntityType().equals("web-page")) {
                e.getAnnotationEntities().addAll(knowledgeBaseDao.findAnnotationEntities(e.getId()));
            }
        }

        return entities;
    }

    @RequestMapping(value = "/getAnnotationEntities/{entityId}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object getAnnotationEntities(@PathVariable("entityId") Integer entityId) throws Exception {
        Collection<AnnotationItem> annotationItems = knowledgeBaseDao.findAnnotationItems(entityId);

//        for (AnnotationItem item : annotationItems) {
//            if (e.getEntityType().equals("web-page")) {
//                e.getAnnotationEntities().addAll(knowledgeBaseDao.findAnnotationEntities(e.getId()));
//            }
//        }

        return annotationItems;
    }

    @RequestMapping(value = "/getAnnotationEntitiesByText", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object getAnnotationEntitiesByText(@RequestBody AnnotationRequest text) throws Exception {
        Collection<AnnotationItem> items = dbpediaSpotlight.annotateText(text.getText());

        return items;
    }

    @RequestMapping(value = "/getEntityList/{userId}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object getEntityList(@PathVariable Long userId) throws Exception {
        Collection<Entity> entities = knowledgeBaseDao.findAllUserEntitiesLazy(userId);

        Collection<UserEntityTopic> topics = knowledgeBaseDao.findAllUserEntityTopics(userId);
        Collection<UserEntityCategory> categories = knowledgeBaseDao.findAllUserEntityCategories(userId);
        Collection<UserEntitySubCategory> subCategories = knowledgeBaseDao.findAllUserEntitySubCategories(userId);

        for (Entity e : entities) {
            for (UserEntityTopic t : topics) {
                if (e.getUserEntityId() != null && e.getUserEntityId().equals(t.getUserEntityId())) {
                    e.addTopic(new Topic(t.getTopicId(), t.getTopicName()));
                }
            }

            for (UserEntityCategory c : categories) {
                if (e.getUserEntityId() != null && e.getUserEntityId().equals(c.getUserEntityId())) {
                    e.addCategory(new Category(c.getCategoryId(), c.getCategoryName()));
                }
            }

            for (UserEntitySubCategory sc : subCategories) {
                if (e.getUserEntityId() != null && e.getUserEntityId().equals(sc.getUserEntityId())) {
                    e.addSubCategory(new SubCategory(sc.getSubcategoryId(), sc.getSubcategoryName()));
                }
            }
        }

        return entities;
    }

    @RequestMapping(value = "/getMainPageEntityList", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object getMainPageEntityList() throws Exception {
        List<Entity> entities = new ArrayList<>(knowledgeBaseDao.findAllEntitiesLazy());

//        for (Entity e : entities) {
//            if (e.getEntityType().equals("web-page")) {
//                e.getAnnotationEntities().addAll(knowledgeBaseDao.findAnnotationEntities(e.getId()));
//            }
//        }

        List<Integer> numbers = new ArrayList<>();
        List<Entity> randomEntities = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            int index = getRandomNumber(entities.size(), numbers);

            if (numbers.contains(index))
                randomEntities.add(entities.get(index));


        }

        return randomEntities;
    }

    public int getRandomNumber(Integer max, List<Integer> numbers) {
        Random r = new Random();
        int num = r.nextInt(max);

        if (numbers.contains(num))
            num = getRandomNumber(max, numbers);

        numbers.add(num);

        return num;
    }

    @RequestMapping(value = "/addEntityToCategory", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object addEntityToCategory(@RequestParam Long userEntityId, @RequestParam Long categoryId) throws Exception {
        knowledgeBaseDao.addEntityToCategory(userEntityId, categoryId);

        return null;
    }

    @RequestMapping(value = "/addEntityToTopic", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object addEntityToTopic(@RequestParam Long userEntityId, @RequestParam Long topicId) throws Exception {
        knowledgeBaseDao.addEntityToTopic(userEntityId, topicId);

        return null;
    }

    @RequestMapping(value = "/addEntityToSubCategory", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object addEntityToSubCategory(@RequestParam Long userEntityId, @RequestParam Long subCategoryId) throws Exception {
        knowledgeBaseDao.addEntityToSubCategory(userEntityId, subCategoryId);

        return null;
    }

    @RequestMapping(value = "/removeEntityFromTopic", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object removeEntityFromTopic(@RequestParam Long userEntityId, @RequestParam Long topicId) throws Exception {
        knowledgeBaseDao.removeEntityFromTopic(userEntityId, topicId);

        return null;
    }

    @RequestMapping(value = "/removeEntityFromCategory", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object removeEntityFromCategory(@RequestParam Long userEntityId, @RequestParam Long categoryId) throws Exception {
        knowledgeBaseDao.removeEntityFromCategory(userEntityId, categoryId);

        return null;
    }

    @RequestMapping(value = "/removeEntityFromSubCategory", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object removeEntityFromSubCategory(@RequestParam Long userEntityId, @RequestParam Long subCategoryId) throws Exception {
        knowledgeBaseDao.removeEntityFromSubCategory(userEntityId, subCategoryId);

        return null;
    }

    @RequestMapping(value = "/annotateOldEntities", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object annotateOldEntities() throws Exception {
        Collection<Entity> entities = knowledgeBaseDao.findAllUserEntitiesLazy(1l);

        for (Entity entity : entities) {
            if (entity.getEntityType().equals("web-page")) {
                entity.getAnnotationEntities().addAll(knowledgeBaseDao.findAnnotationEntities(entity.getId()));
            }

            if (entity.getAnnotationEntities().size() == 0 && entity.getDescription() != null && entity.getDescription().length() > 0) {
                Collection<AnnotationItem> items = dbpediaSpotlight.annotateText(entity.getDescription());

                for (AnnotationItem i : items) {
                    i.setReferencedEntityId(entity.getId());
                }

                try {
                    knowledgeBaseDao.saveAnnotationItems(new ArrayList<AnnotationItem>(items));
                } catch (Exception e) {
                    System.err.println("ERROR at dbpediaspotlight, saving annotation items...");
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    @RequestMapping(value = "/saveTopic/{name}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object saveTopic(@PathVariable String name, HttpServletRequest request) throws Exception {
        Long userId = (Long) request.getSession().getAttribute("userId");

        return knowledgeBaseDao.saveTopic(name, userId);
    }

    @RequestMapping(value = "/saveCategory/{topicId}/{name}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object saveCategory(@PathVariable Long topicId, @PathVariable String name, HttpServletRequest request) throws Exception {
        return knowledgeBaseDao.saveCategory(topicId, name);
    }

    @RequestMapping(value = "/saveSubcategory/{categoryId}/{name}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object addSubcategory(@PathVariable Long categoryId, @PathVariable String name) throws Exception {
        return knowledgeBaseDao.saveSubcategory(categoryId, name);
    }

    @RequestMapping(value = "/autocompleteProperty/{prefix}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object autocompleteProperty(@PathVariable("prefix") String prefix) throws Exception {
        Collection<MetaProperty> metaProperties = knowledgeBaseDao.findPropertiesByPrefix(prefix);

        List<MetaProperty> withoutSameAs = new ArrayList<>();

        for (MetaProperty mp1 : metaProperties) {
            if (mp1.getSameAs() == null || mp1.getSameAs() == 0) {
                withoutSameAs.add(mp1);
            } else {
                boolean sameAsExists = false;
                for (MetaProperty mp2 : metaProperties) {
                    if (mp2.getSameAs() != null && mp1.getSameAs().equals(mp2.getSameAs())) {
                        sameAsExists = true;
                    }
                }

                if (!sameAsExists) {
                    withoutSameAs.add(mp1);
                }
            }
        }

        return withoutSameAs;
    }

    @RequestMapping(value = "/getEntityById/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object getEntityById(@PathVariable(value = "id") Long id, HttpServletRequest request) throws Exception {
        Long userId = (Long) request.getSession().getAttribute("userId");

        Entity entity = knowledgeBaseDao.findEntityById(id);

        if (entity.getEntityType().equals("web-page")) {
            entity.getAnnotationEntities().addAll(knowledgeBaseDao.findAnnotationEntities(entity.getId()));
        }

        if (userId != null) {
            List<Topic> topics = new ArrayList<>();
            for (UserEntityTopic uet : knowledgeBaseDao.findAllUserEntityTopics(userId, id)) {
                topics.add(new Topic(uet.getTopicId(), uet.getTopicName()));
            }

            List<Category> categories = new ArrayList<>();
            for (UserEntityCategory uec : knowledgeBaseDao.findAllUserEntityCategories(userId, id)) {
                categories.add(new Category(uec.getCategoryId(), uec.getCategoryName()));
            }

            List<SubCategory> subCategories = new ArrayList<>();
            for (UserEntitySubCategory ues : knowledgeBaseDao.findAllUserEntitySubCategories(userId, id)) {
                subCategories.add(new SubCategory(ues.getSubcategoryId(), ues.getSubcategoryName()));
            }

            entity.setTopics(topics);
            entity.setCategories(categories);
            entity.setSubCategories(subCategories);
        }

        return entity;
    }

    @RequestMapping(value = "/getAllMetaproperties", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object getAllMetaproperties() throws Exception {
        Collection<MetaProperty> metaProperties = knowledgeBaseDao.findAllMetaproperties();

        return metaProperties;
    }

    @RequestMapping(value = "/getAllTopics/{userId}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object getAllTopics(@PathVariable Long userId) throws Exception {
        Collection<Topic> topics = knowledgeBaseDao.findAllTopics(userId);

        return topics;
    }

    @RequestMapping(value = "/getAllCategories/{userId}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object getAllCategories(@PathVariable Long userId) throws Exception {
        Collection<Category> categories = knowledgeBaseDao.findAllCategories(userId);

        return categories;
    }

    @RequestMapping(value = "/getSubCategories/{categoryId}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object getSubCategories(@PathVariable Long categoryId) throws Exception {
        Collection<SubCategory> subCategories = knowledgeBaseDao.findSubCategories(categoryId);

        return subCategories;
    }

    @RequestMapping(value = "/getAllSubCategories/{userId}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object getAllSubCategories(Long userId) throws Exception {
        Collection<SubCategory> subCategories = knowledgeBaseDao.findAllSubCategories(userId);

        return subCategories;
    }

    @RequestMapping(value = "/getLoggedInUser", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Object getLoggedInUser(HttpServletRequest request) throws Exception {
        String username = (String) request.getSession().getAttribute("username");

        if (username == null) {
            return -1;
        }

        User user = knowledgeBaseDao.findUserByUsername(username);

        return user;
    }
}