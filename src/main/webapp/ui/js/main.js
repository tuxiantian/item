var require = {
    baseUrl: 'ui/js/',
    paths: {
        'text':'requireJs/2.1.17/text', 
        'style':'requireJs/2.1.17/css.min', 
        'json':'requireJs/2.1.17/json',
        'domReady':'requireJs/2.1.17/domReady'
        
        // 'jquery':'jquery/1.8.3/jquery',
        // 'handlebars':'handlebars/handlebars', 
        // 'handlebars.helpers':'handlebars/helpers', 
        // 'dialog':'dialog/6.0.5/dialog-min',
        // 'jquery.blockUI':'blockUI/jquery.blockUI', 
        // 'jquery.pagination':'pagination/1.2.1/jquery.pagination', 
        // 'selectordie':'selectordie/0.1.8/selectordie.min'
    },
    shim: {
        //The jquery script dependency should be loaded before loading artDialog.js
        
       /* 'handlebars.helpers':{
            deps:['handlebars']
        }, 
        'jquery.blockUI':{
            deps: ['jquery']
        }, 
        'jquery.pagination':{
            deps: ['jquery']
        }, 
        'dialog':{
            deps: ['jquery'],
            exports: 'dialog'
        }, 
        'handlebars':{
            exports:'Handlebars'
        }, */
        'jquery':{
            exports:'jQuery'
        }
    }
};

