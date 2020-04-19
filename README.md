To generate the APIkey follow the below instructions:
1) Create an account in www.themoviedb.org. While creating state that your usage will be for educational/non-commercial use.
You will also need to provide some personal information to complete the request.Once you submit your request, you should receive your key via email shortly after.
2) Then login to www.themoviedb.org and then click on your name-->Settings-->API menu on the left.
You can also see the API key on the right.


Please replace this API key in the below variable in NetworkUtils.java

 1) private static String movieDbPopularUrl = "http://api.themoviedb.org/3/movie/popular?api_key=[API_KEY]"
  
 2) private static String movieDbRatingUrl = "http://api.themoviedb.org/3/movie/top_rated?api_key=[API_KEY]"
 
 
 

