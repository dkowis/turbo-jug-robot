package org.shlrm.jugbot

import javax.net.ssl.{TrustManagerFactory, KeyManagerFactory, SSLContext}
import java.security.{SecureRandom, KeyStore}
import spray.io.ServerSSLEngineProvider

//Enables SSL support (if the configuration turns it on)
trait MySslConfiguration {

  //if there's no SSLContext ins cope implicitly, the HttpServer will use the default SSL context,
  // Since we want non-default settings in this example, we make a custom SSLContext
  implicit def sslContext: SSLContext = {
    val keyStoreResource = "/ssl-keystore.jks"
    val password = ""

    val keyStore = KeyStore.getInstance("jks")
    keyStore.load(getClass.getResourceAsStream(keyStoreResource), password.toCharArray)
    val keyManagerFactory = KeyManagerFactory.getInstance("SunX509")
    keyManagerFactory.init(keyStore, password.toCharArray)
    val trustManagerFactory = TrustManagerFactory.getInstance("SunX509")
    trustManagerFactory.init(keyStore)
    val context = SSLContext.getInstance("TLS")
    context.init(keyManagerFactory.getKeyManagers, trustManagerFactory.getTrustManagers, new SecureRandom)
    context
  }

  // if there is no ServerSSLEngineProvider in scope implicitly the HttpServer uses the default one,
  // since we want to explicitly enable cipher suites and protocols we make a custom ServerSSLEngineProvider
  // available here
  implicit def sslEngineProvider: ServerSSLEngineProvider = {
    ServerSSLEngineProvider {
      engine =>
        engine.setEnabledCipherSuites(Array("TLS_RSA_WITH_AES_256_CBC_SHA"))
        engine.setEnabledProtocols(Array("SSLv3", "TLSv1"))
        engine
    }
  }

}
