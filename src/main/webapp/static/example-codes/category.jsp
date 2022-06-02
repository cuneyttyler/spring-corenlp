<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="${language}"/>
<fmt:setBundle basename="com.istiletisim.showroomist.texts.texts"/>

<section class="blog-overview-1">
    <div class="container">
        <div class="row">
            <div class="col-md-8 posts">
                <div class="row">
                    <div class="col-md-6">
                        <h3><fmt:message key="blog.category.addcategory"/></h3>
                        <hr>
                    </div>
                    <div class="col-md-6">
                        <h3><fmt:message key="blog.category.categories"/></h3>
                        <hr>
                    </div>
                </div>

                <div class="row">

                    <div class="col-md-6">
                        <div class="row">

                            <div class="col-md-12">
                                <div class="form-group">
                                    <input type="text" ng-model="cat_name" class="form-control input-md"
                                           placeholder="<fmt:message key="blog.category.category"/>">
                                </div>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-md-12">
                                <button ng-click="saveCategory()" type="button" class="btn ghost-btn -gray">
                                    <fmt:message key="blog.category.savecategory"/>
                                </button>
                            </div>
                        </div>
                    </div>

                    <div class="col-md-6">
                        <div class="sidebar-widget categories">
                            <ul>
                                <li ng-repeat="cat in categories">
                                    <div class="row">
                                        <div class="col-md-10" ng-show="!cat.edit">
                                            {{cat.name}}
                                        </div>
                                        <div class="col-md-10" ng-show="cat.edit">
                                            <input type="text" ng-model="cat.newName" class="form-control input-md"
                                                   ng-placeholder="cat.name">
                                        </div>

                                        <div class="col-md-1">
                                            <a href>
                                            <span ng-click="editCategory(cat)" ng-show="!cat.edit">
                                                <fmt:message key="global.edit"/>
                                            </span>
                                            </a>
                                            <a href>
                                            <span ng-click="modifyCategory(cat)" ng-show="cat.edit">
                                                <fmt:message key="global.save"/>
                                            </span>
                                            </a>
                                            <a href>
                                            <span ng-click="removeCategory(cat.id)">
                                                <fmt:message key="global.remove"/>
                                            </span>
                                            </a>
                                        </div>
                                    </div>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>

            <div class="col-md-3 col-md-offset-1 sidebar">
                <div class="row">
                    <div class="col-md-12">
                        <div class="form-group">
                            <input type="text" class="form-control input-lg"
                                   placeholder="<fmt:message key="blog.sidebar.search"/>" ng-model="searchText"
                                   my-enter="search()">
                        </div>
                    </div>
                    <div class="col-md-12">
                        <div class="sidebar-widget categories">
                            <h3 class="sidebar-title"><fmt:message key="blog.sidebar.categories"/></h3>
                            <ul>
                                <li ng-repeat="cat in categories">
                                    <a href ng-click="categoryClicked(cat)">{{cat.name}}</a>
                                </li>
                            </ul>
                        </div>

                        <div class="sidebar-widget latest-posts">
                            <h3 class="sidebar-title"><fmt:message key="blog.sidebar.latestposts"/></h3>
                            <ul>
                                <li ng-repeat="post in latestPosts"><a href ng-click="openPostPage(post.id)">{{post.title}}<br>
                                    <small>{{post.formattedDate}}</small>
                                </a></li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>


    </div>
    </div>
</section>

</br>


<script type="text/ng-template" id="confirmWindow">

    <!-- Page Content #F5F5F5 -->
    <div class="col-md-4 col-md-offset-4 pop-up">
        <!-- Simple Blocks -->

        <div class="block margin-bottom0">
            <div class="block-header bg-gray-lighter">
                <h3 class="block-title"><fmt:message key="blog.category.removeConfirmTitle"/></h3>
            </div>
            <div class="block-content block-content-full">
                <div class="row">
                    <div class="col-md-12">
                        <fmt:message key="blog.category.removeConfirmTitle"/>
                    </div>
                </div>
            </div>
            <br>

            <div class="row padding-bottom10">
                <div class="col-md-2">
                    <div class="form-group">
                        <%--<div class="col-md-12">--%>
                        <button id="button" type="button" class="btn ghost-btn -gray" ng-click="yes()"><fmt:message
                                key="global.yes"/>
                        </button>
                        <%--</div>--%>
                    </div>
                </div>
                <div class="col-md-8">
                    <div class="form-group">
                        <%--<div class="col-md-12">--%>
                        <button id="button" type="button" class="btn ghost-btn -gray" style="margin-left:10px;"
                                ng-click="no()"><fmt:message key="global.no"/>
                        </button>
                        <%--</div>--%>
                    </div>
                </div>
            </div>
        </div>
    </div>
</script>