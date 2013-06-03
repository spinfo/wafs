<div id="header">
	<%@ taglib prefix='cr' uri='http://java.sun.com/jstl/core_rt'%>

	<div class="navbar" id="navigation">
		<div class="navbar-inner">
			<div class="container">
				<a class="brand active">WAFS Music Player</a>
				<ul class="nav">
					<li><a href="/artists.html">Artists</a></li>
					<li><a href="/albums.html">Albums</a></li>
					<li><a href="#">etc...</a></li>
				</ul>
				<div class="nav pull-right" id="nav_menus">
				<%
	
		String searchPhrase =  request.getParameter("search");
	%>
				    <form class="navbar-search pull-left" action="/search.html">
				    	<input type="text" class="search-query" placeholder="Search" name="search" value="<%=searchPhrase == null ? "" : searchPhrase %>">
				    </form>
				</div>
			</div>
		</div>
	</div>




</div>