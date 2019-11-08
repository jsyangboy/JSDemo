(
    function(){
        window.LZGLJSBridge =function(){
            var callbacks = [];
            var callbackID = 0;
            var registerHandlers= [];
            document.addEventListener('LZGLNativeCallback', function (e) {
                        if (e.detail) {
                            var detail = e.detail;
                            var id = isNaN(parseInt(detail.id)) ? -1 : parseInt(detail.id)
                            if (id != -1) {
                                callbacks[id] && callbacks[id](detail.ret);
                                delete callbacks[id];
                            }
                        }
                    }, false);
            document.addEventListener('LZGLNativeNotification', function (e) {
                        if (e.detail) {
                            var detail = e.detail;
                            var name = detail.name
                            if (name !== undefined && registerHandlers[name]) {
                                var namedListeners = registerHandlers[name]
                                if (namedListeners instanceof Array) {
                                    var ret = detail.ret
                                    namedListeners.forEach(function (handler) {
                                        handler(ret)
                                    })
                                }
                            }
                        }
                    }, false);

            return {
                'call':function(method ,params, callback){
                    var id = callbackID++;
                    callbacks[id] = callback;

                    JsBridge.log("id="+id);
                    JsBridge.postMessage(method,params,id);
                },
                'callback':function(value){
                    JsBridge.log(value);

                    var event = new CustomEvent('LZGLNativeCallback', JSON.parse(value));
                    document.dispatchEvent(event)
                },
                'on':function(name, callback){
                     var namedListeners = registerHandlers[name]
                     if (!namedListeners) {
                        registerHandlers[name] = namedListeners = []
                     }
                     namedListeners.push(callback);
                     JsBridge.log(name);
                     return function () {
                         namedListeners[indexOf(namedListeners, callback)] = null
                     }
                }
            }
        }()
    }()

);

