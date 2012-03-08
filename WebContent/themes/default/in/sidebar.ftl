<div id="side_bar">
	<div class="widget">
		<form action="${blogurl!appPath}/search" method="get">
			<input type="input" name="keywords" class="input">
			<input type="submit" class="btn" value="搜索">
		</form>
	</div>
	
	<div class="widget hava_red_a_black">
		<h4>分类目录</h4>
		<#if blog.categorys?has_content>
		<ul class="cat_list">
		<#list blog.categorys as c>
			<li>
				<a href="${blogurl!appPath}/category/${c.term_taxonomy_id}/${c.name}">
					${c.name}
					&nbsp;&nbsp;
					(${c.count})
				</a>
			</li>
		</#list>
		</ul>
		<#else>
		没有内容!!
		</#if>
	</div>
	
	<div class="widget hava_red_a_black">	
		<h4>最新评论</h4>
		<#if blog.latestComments?has_content>
		<ul class="cat_list">
		<#list blog.latestComments as c>
			<li>
				<a href="${blogurl!appPath}/post/${c.comment_post_id}.html">
					${c.comment_content}
				</a>
			</li>
		</#list>
		</ul>
		<#else>
		没有内容!!
		</#if>
	</div>
	
	<div class="widget hava_red_a_black">	
		<h4>最新文章</h4>
		<#if blog.latestPosts?has_content>
		<ul class="cat_list">
		<#list blog.latestPosts as p>
			<li><a href="${blog.url(p)}">${p.post_title}</a></li>
		</#list>
		</ul>
		<#else>
		没有内容!!
		</#if>
	</div>
	
	<div class="widget hava_red_a_black">	
		<h4>随机文章</h4>
		<#if blog.randomPosts?has_content>
		<ul class="cat_list">
		<#list blog.randomPosts as p>
			<li><a href="${blog.url(p)}">${p.post_title}</a></li>
		</#list>
		</ul>
		<#else>
		没有内容!!
		</#if>
	</div>
	
	<div class="widget hava_red_a_black">	
		<h4>友情连接</h4>
		<#if blog.links?has_content>
		<ul class="cat_list">
		<#list blog.links as l>
			<li><a href="${l.link_url}" target="${l.link_target}" title="${l.link_description}">${l.link_name}</a></li>
		</#list>
		</ul>
		<#else>
		没有内容!!
		</#if>
	</div>
</div>