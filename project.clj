(defproject clojush "1.2.6"
            :description "The Push programming language and the PushGP genetic programming
                          system implemented in Clojure. 
                          See http://hampshire.edu/lspector/push.html"
            :dependencies [[org.clojure/clojure "1.3.0"]
                           [org.clojure/math.numeric-tower "0.0.1"]   
                           [local-file "0.0.4"]]
	    :jvm-opts ["-Xmx500m"]
            :main clojush.core)
