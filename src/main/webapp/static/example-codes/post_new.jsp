<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="${language}"/>
<fmt:setBundle basename="com.istiletisim.showroomist.texts.texts"/>

<section class="blog-overview-1">
    <div class="container">
        <div class="row">
            <div class="col-md-3 sidebar">
                <div class="row">
                    <div class="col-md-12">
                        <div class="sidebar-widget categories">
                            <h3 class="sidebar-title"><fmt:message key="blog.new.category"/></h3>
                            <select class="form-control" ng-model="selectedCategory">
                                <option ng-value="0"><fmt:message key="blog.new.category.none"/></option>
                                <option ng-repeat="c in categories" ng-value="c.id">{{c.name}}</option>
                            </select>
                        </div>
                    </div>
                    <div class="col-md-12">
                        <div class="sidebar-widget categories">
                            <h3 class="sidebar-title"><fmt:message key="blog.new.tags"/></h3>

                            <input class="" id="tag_input_element" type="text" name="tag_input_element" width="200"/>
                            <%--<div style="clear:both;"></div>--%>
                        </div>
                    </div>
                    <div class="col-md-12">
                        <div class="sidebar-widget categories">
                            <h3 class="sidebar-title"><fmt:message key="blog.new.products"/></h3>
                            <ul>
                                <li ng-repeat="product in products"
                                    ng-attr-id="{{ product.type + '-' + product.id}}">
                                    <a href style="color: #bebd24;">{{product.name}}</a>

                                    <div class="image-popover">
                                        <a href ng-click="addImage($event,product,product.image)">
                                            <img class="image" ng-src="{{product.image}}"/>
                                        </a>
                                        <a href ng-click="addImage($event,product,product.image2)">
                                            <img class="image" ng-src="{{product.image2}}"/>
                                        </a>
                                    </div>

                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>

            <div class="col-md-8">
                <div class="row">

                    <div class="col-md-12">
                        <div class="form-group">
                            <input type="text" ng-model="title" class="form-control input-lg"
                                   placeholder="<fmt:message key="blog.new.title"/>">
                        </div>
                    </div>
                </div>

                <div class="row">
                    <div class="col-md-12">
                        <textarea name="editor" style="display:none;"></textarea>
                    </div>
                </div>

                <br/>

                <div class="row">
                    <div class="col-md-12">
                        <button ng-click="preview()" type="button" class="btn ghost-btn -gray"><fmt:message
                                key="blog.new.preview"/></button>
                    </div>
                </div>

                <br>

                <div class="row" ng-show="errorMessage.length > 0">
                    <div class="col-md-12 alert alert-warning">
                        <p class="">{{errorMessage}}</p>
                    </div>
                </div>

            </div>
        </div>
    </div>
</section>