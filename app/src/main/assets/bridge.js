(
    function(){
        window.LZGLJSBridge =function(message){
            JsBridge.log(message);



            return {
                'call':function(){
                    JsBridge.log(message);
                },
                'on':function(name, callback){
                    JsBridge.log(callback);
                }
            }
        }()
    }()

);


