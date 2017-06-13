//
// jquery.alertable.js - Minimal alert and confirmation alternatives.
//
// Developed by Cory LaViska for A Beautiful Site, LLC
//
// Licensed under the MIT license: http://opensource.org/licenses/MIT
//
if(jQuery) (function($) {
    'use strict';

    var modal,
        overlay,
        okButton,
        cancelButton,
        activeElement;

    function show(type, message, options) {
        var defer = $.Deferred();

        // Remove focus from the background
        activeElement = document.activeElement;
        activeElement.blur();

        // Remove other instances
        $(modal).add(overlay).remove();

        // Merge options
        options = $.extend({}, $.alertable.defaults, options);

        // Create elements
        modal = $(options.modal).hide();
        overlay = $(options.overlay).hide();
        okButton = $(options.okButton);
        cancelButton = $(options.cancelButton);

        // Add message
        if( options.html ) {
            modal.find('.alertable-message').html(message);
        } else {
            modal.find('.alertable-message').text(message);
        }

        // Add buttons
        $(modal).find('.alertable-buttons')
        .append(type === 'alert' ? '' : cancelButton)
        .append(okButton);

        // Add to container
        $(options.container).append(overlay).append(modal);

        // Show it
        options.show.call({
            modal: modal,
            overlay: overlay
        });

        // Focus on last input (should be OK button)
        $(modal).find(':input:last').focus();

        // Watch for OK
        okButton.on('click.alertable', function() {
            hide(options);
            defer.resolve();
        });

        // Watch for cancel
        cancelButton.on('click.alertable', function() {
            hide(options);
            defer.reject();
        });

        // Watch for key presses
        $(document).on('keydown.alertable', function(event) {
            // Enter || Escape
            if( event.keyCode === 13 || event.keyCode === 27 ) {
                event.preventDefault();
                hide(options);
                if( event.keyCode === 13 && !$(event.target).is(cancelButton) ) {
                    defer.resolve();
                } else {
                    defer.reject();
                }
            }
        });

        // Prevent focus from leaving the modal
        $(document).on('focus.alertable', '*', function(event) {
            if( !$(event.target).parents().is('.alertable') ) {
                event.stopPropagation();
                event.target.blur();
                $(modal).find(':input:first').focus();
            }
        });

        return defer.promise();
    }

    function hide(options) {
        // Hide it
        options.hide.call({
            modal: modal,
            overlay: overlay
        });

        // Remove bindings
        $(document).off('.alertable');
        okButton.off('.alertable');
        cancelButton.off('.alertable');

        // Restore focus
        activeElement.focus();
    }

    // Defaults
    $.alertable = {
        // Show an alert
        alert: function(message, options) {
            return show('alert', message, options);
        },

        // Show a confirmation
        confirm: function(message, options) {
            return show('confirm', message, options);
        },

        defaults: {
            // Preferences
            container: 'body',
            html: false,

            // Templates
            cancelButton: '<button class="alertable-cancel" type="button">取消</button>',
            okButton: '<button class="alertable-ok" type="button">确定</button>',
            overlay: '<div class="alertable-overlay"></div>',
            modal:
                '<div class="alertable">' +
                '<div class="alertable-message"></div>' +
                '<div class="alertable-buttons"></div>' +
                '</div>',

            // Hooks
            hide: function() {
                $(this.modal).add(this.overlay).fadeOut(100);
            },
            show: function() {
                $(this.modal).add(this.overlay).fadeIn(100);
            }
        }
    };
})(jQuery);