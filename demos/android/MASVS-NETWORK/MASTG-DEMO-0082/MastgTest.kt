package org.owasp.mastestapp

import android.content.Context
import android.net.Uri
import android.media.MediaPlayer
import java.net.URL
import java.net.URI

data class Schemes(
    val insecure: String,
    val secure: String
)

class MastgTest(private val context: Context) {

    fun mastgTest(): String {
        val r = DemoResults("0082")
        val host = "mas.owasp.org"

        val schemes = listOf(
            Schemes("http", "https"),
            Schemes("ws", "wss"),
            Schemes("sip", "sips"),
            Schemes("ldap", "ldaps"),
            Schemes("ftp", "ftps"),
            Schemes("rtsp", "rtsps"),
            Schemes("stun", "stuns")
        )

        schemes.forEach { scheme ->
            val secureUri = "${scheme.secure}://$host"
            val insecureUri = "${scheme.insecure}://$host"

            // java.net.URL throws an exception if the scheme is not http, https or ftp
            if( scheme.insecure == "http" || scheme.insecure == "ftp" ){

                // java.net.URL(String spec)
                try {
                    val test = URL(secureUri)
                    print(test)
                    r.add(Status.PASS, "Secure URL created with $secureUri (String spec)")
                } catch (e: Exception) {
                    r.add(Status.ERROR, e.toString())
                }
                try {
                    URL(insecureUri)
                    r.add(Status.FAIL, "Insecure URL created with $insecureUri (String spec)")
                } catch (e: Exception) {
                    r.add(Status.ERROR, e.toString())
                }

                // java.net.URL(String protocol, String host, int port, String file)
                try {
                    URL(scheme.secure, host, 443, "/")
                    r.add(Status.PASS, "Secure URL created with ${scheme.secure}://$host:443/ (String protocol, String host, int port, String file)")
                } catch (e: Exception) {
                    r.add(Status.ERROR, e.toString())
                }
                try {
                    URL(scheme.insecure, host, 80, "/")
                    r.add(Status.FAIL, "Insecure URL created with ${scheme.insecure}://$host:80/ (String protocol, String host, int port, String file)")
                } catch (e: Exception) {
                    r.add(Status.ERROR, e.toString())
                }

                // java.net.URL(String protocol, String host, int port, String file, URLStreamHandler handler)
                try {
                    URL(scheme.secure, host, 443, "/", null)
                    r.add(Status.PASS, "Secure URL created with ${scheme.secure}://$host:443/ (String protocol, String host, int port, String file, URLStreamHandler handler)")
                } catch (e: Exception) {
                    r.add(Status.ERROR, e.toString())
                }
                try {
                    URL(scheme.insecure, host, 80, "/", null)
                    r.add(Status.FAIL, "Insecure URL created with ${scheme.insecure}://$host:80/ (String protocol, String host, int port, String file, URLStreamHandler handler)")
                } catch (e: Exception) {
                    r.add(Status.ERROR, e.toString())
                }

                // java.net.URL(String protocol, String host, String file)
                try {
                    URL(scheme.secure, host, "/")
                    r.add(Status.PASS, "Secure URL created with ${scheme.secure}://$host/ (String protocol, String host, String file)")
                } catch (e: Exception) {
                    r.add(Status.ERROR, e.toString())
                }
                try {
                    URL(scheme.insecure, host, "/")
                    r.add(Status.FAIL, "Insecure URL created with ${scheme.insecure}://$host/ (String protocol, String host, String file)")
                } catch (e: Exception) {
                    r.add(Status.ERROR, e.toString())
                }
            }


            // java.net.URI(String spec)
            try {
                URI(secureUri)
                r.add(Status.PASS, "Secure URI created with $secureUri (String spec)")
            } catch (e: Exception) {
                r.add(Status.ERROR, e.toString())
            }
            try {
                URI(insecureUri)
                r.add(Status.FAIL, "Insecure URI created with $insecureUri (String spec)")
            } catch (e: Exception) {
                r.add(Status.ERROR, e.toString())
            }

            // java.net.URI(String scheme, String ssp, String fragment)
            try {
                URI(scheme.secure, "//$host/", null)
                r.add(Status.PASS, "Secure URI created with ${scheme.secure}://$host/ (String scheme, String ssp, String fragment)")
            } catch (e: Exception) {
                r.add(Status.ERROR, e.toString())
            }
            try {
                URI(scheme.insecure, "//$host/", null)
                r.add(Status.FAIL, "Insecure URI created with ${scheme.insecure}://$host/ (String scheme, String ssp, String fragment)")
            } catch (e: Exception) {
                r.add(Status.ERROR, e.toString())
            }

            // java.net.URI(String scheme, String host, String path, String fragment)
            try {
                URI(scheme.secure, host, "/", null)
                r.add(Status.PASS, "Secure URI created with ${scheme.secure}://$host/ (String scheme, String host, String path, String fragment)")
            } catch (e: Exception) {
                r.add(Status.ERROR, e.toString())
            }
            try {
                URI(scheme.insecure, host, "/", null)
                r.add(Status.FAIL, "Insecure URI created with ${scheme.insecure}://$host/ (String scheme, String host, String path, String fragment)")
            } catch (e: Exception) {
                r.add(Status.ERROR, e.toString())
            }

            // java.net.URI(String scheme, String authority, String path, String query, String fragment)
            try {
                URI(scheme.secure, host, "/", null, null)
                r.add(Status.PASS, "Secure URI created with ${scheme.secure}://$host/ (String scheme, String authority, String path, String query, String fragment)")
            } catch (e: Exception) {
                r.add(Status.ERROR, e.toString())
            }
            try {
                URI(scheme.insecure, host, "/", null, null)
                r.add(Status.FAIL, "Insecure URI created with ${scheme.insecure}://$host/ (String scheme, String authority, String path, String query, String fragment)")
            } catch (e: Exception) {
                r.add(Status.ERROR, e.toString())
            }

            // android.net.Uri.parse(String uriString)
            try {
                Uri.parse(secureUri)
                r.add(Status.PASS, "Secure Uri created with $secureUri (Uri.parse)")
            } catch (e: Exception) {
                r.add(Status.ERROR, e.toString())
            }
            try {
                Uri.parse(insecureUri)
                r.add(Status.FAIL, "Insecure Uri created with $insecureUri (Uri.parse)")
            } catch (e: Exception) {
                r.add(Status.ERROR, e.toString())
            }

            // android.net.Uri.Builder.scheme(String scheme)
            try {
                Uri.Builder().scheme(scheme.secure).authority(host).build()
                r.add(Status.PASS, "Secure Uri created with ${scheme.secure}://$host (Uri.Builder.scheme)")
            } catch (e: Exception) {
                r.add(Status.ERROR, e.toString())
            }
            try {
                Uri.Builder().scheme(scheme.insecure).authority(host).build()
                r.add(Status.FAIL, "Insecure Uri created with ${scheme.insecure}://$host (Uri.Builder.scheme)")
            } catch (e: Exception) {
                r.add(Status.ERROR, e.toString())
            }

            // android.media.MediaPlayer.setDataSource(String path)
            try {
                val mediaPlayer = MediaPlayer()
                mediaPlayer.setDataSource(secureUri)
                r.add(Status.PASS, "Secure data source set with $secureUri (MediaPlayer.setDataSource)")
                mediaPlayer.release()
            } catch (e: Exception) {
                r.add(Status.ERROR, e.toString())
            }

            try {
                val mediaPlayer2 = MediaPlayer()
                mediaPlayer2.setDataSource(insecureUri)
                r.add(Status.FAIL, "Insecure data source set with $insecureUri (MediaPlayer.setDataSource)")
                mediaPlayer2.release()
            } catch (e: Exception) {
                r.add(Status.ERROR, e.toString())
            }
        }

        return r.toJson()
    }
}
