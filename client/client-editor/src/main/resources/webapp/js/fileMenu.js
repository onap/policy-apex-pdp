/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

/*global define,jQuery,window */

(function(factory) {

    "use strict";

    if (typeof (define) === 'function' && define.amd) {
        define([ 'jquery' ], factory);
    } else {
        factory(jQuery);
    }
}(function($) {

    "use strict";

    $.fileMenu = function(el, options) {

        var base = this;

        base.$el = $(el);
        base.el = el;

        base.options = $.extend({}, $.fileMenu.defaultOptions, options);
        base.touch = false;

        base.$ = function(query) {
            return base.$el.find(query);
        };

        base.hideMenu = function() {
            base.$('.selected ul').slideUp(base.options.slideSpeed);
            base.$('.selected').removeClass('selected');
            base.$el.removeClass('active');
        };

        base.showMenu = function($this) {
            var $parent = $this.parent(), $menu = $this.children('ul').first(), offsets = $this.offset();

            $parent.addClass('active');

            $this.addClass('selected');
            $menu.css({
                'left' : offsets.left,
                'top' : offsets.top + $parent.height()
            });
            $menu.slideDown(base.options.slideSpeed);
        };

        base.addListeners = function() {
            base.$el.children('li').on('click', function(e) {
                var $this = $(this);

                if ($this.hasClass('selected')) {
                    base.hideMenu();
                } else {
                    base.hideMenu();
                    base.showMenu($this);
                }
                e.stopPropagation();
            });

            base.$el.children('li').on('mouseenter', function() {
                var $this = $(this);
                if (!$this.parent().hasClass('active')) {
                    return;
                }
                if ($this.hasClass('selected')) {
                    return;
                }

                base.hideMenu();
                base.showMenu($this);
            });

            /* Don't slide up if submenu, divider or disabled item is clicked */
            base.$('ul').on('click', function(e) {
                var $this = $(e.target);
                if ($this.get(0).tagName === 'LI' && !$this.hasClass('sub')) {
                    return;
                }
                e.stopPropagation();
            });

            /* Handle toggle elements */
            base.$('ul').on('click', '.toggle', function(e) {
                $(this).toggleClass('active');
                e.stopPropagation();
            });

            /* Position sub menus */
            base.$el.on('mouseenter', 'ul li', function() {
                var $this = $(this);

                $this.find('ul').first().css({
                    'left' : $this.parent().width(),
                    'margin-top' : -$this.height()
                });
            });

            /* Hide menu on click outside the menu */
            $('html').on('click', function() {
                base.hideMenu();
            });
        };

        base.init = function() {
            base.addListeners();
            base.$el.addClass('fileMenu');

        };

        base.init();
    };

    $.fileMenu.defaultOptions = {
        slideSpeed : 100
    };

    $.fn.fileMenu = function(options) {
        return this.each(function() {
            var fm = new $.fileMenu(this, options);
        });
    };

}));