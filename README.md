# Web Crawler using Spring Boot and Completable Futures & EhCache

An example application using Spring boot and Completable Future & EhCache

## Technology stack
-   Spring Boot
-   Swagger
-   Spring Boot Test/JUnit/Mockito/RestAssured

## Build Instruction
- Maven
- JDK 8 or above

## Assumptions
- Though the application supports deep crawling, I have decided to limit that upto the level of 5 for performance reasons. Also the breadth of the pagelinks on a particular page is limited to 20 for the same reason. This can be relaxed by modifying property in "src/main/resources/application.yml" or setting that as JVM property i.e: Depth -> 'webcrawler.default.depth.max.limit' & Breadth -> 'webcrawler.default.breadth.max.limit'.
- The HTTP errors while performing the page download is handled gracefully and the exception reason is included as that page title just to indicate what went wrong with the URL. This may not be the desired production behaviour and this app is built for demo purpose to show the user how the functionality can be implemented, however the application is designed to support any changes.
- The Proxy settings can be done by setting JVM properties as explained in this readme. It is assumed that the proxy settings will be done by who ever testing this.
- Jsoup is used to download and parse the html content. Redirects are not set to false. Also any page link download will timeout in 5 seconds. This also can be modified by setting jvm property 'webcrawler.default.individual.page.download.timeout.in.millis'

## Run

### Internet Without proxy
mvn spring-boot:run

### Internet With proxy

|   JVM Property     |Description           
|----------------|------------------------
|http.proxyHost  | proxy host name        
|https.proxyHost | https proxy host name  
|http.proxyPort  | proxy port number      
|https.proxyPort | https proxy port number
|http.proxyUser  | proxy user name        
|https.proxyUser | https proxy user name  
|http.proxyPassword  | proxy user password
|https.proxyPassword | https proxy user password


Pass all applicable jvm arguments
 
mvn -D[jvmProperty]=[value] spring-boot:run


### Testing

Endpoint:

http://localhost:8080/crawl?depth=5&breadth=10&url=https://example.com

|  Query Param name       |Description           |
|----------------|------------------------|
|url             | [Mandatory] url to crawl        |
|depth | Depth of the page links to be crawled, default is 2 max 10  |
|breadth  | Max number of page links to be considered to crawl, default is 10 and max 20      |

