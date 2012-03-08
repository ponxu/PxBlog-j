<div id="main_content">
	<#if post??>
		<div class="post_list hava_red_a_black">
			<h3><a href="${blog.url(post)}">${post.post_title}</a></h3>
			<p class="info">
				时间: ${post.post_date?substring(0, 19)}
				&nbsp;&nbsp;&nbsp;
				评论: <a href="#comments">${post.comment_count}</a>
			</p>
			<div class="abs">
				${post.post_content}
			</div>
			<p class="info">
				分类/标签:
				<#if post_terms?has_content>
					<#list post_terms as t>
						<#if t.object_id=post.id>
							<a href="${blog.g('blogurl')}/category/${t.term_taxonomy_id}/${t.name}">${t.name}</a>
						</#if>
					</#list>
				</#if>
			</p>
		</div>
		<#if post.comment_status='open'>
			<p class="info hava_red_a_black">
				<a href="#comment_add">我要评论</a>
			</p>
			<#include "../comment.ftl">
			<p class="info hava_red_a_black">
				<a href="#" onclick="loadComments()">刷新</a>
			</p>
			<#include "comment_form.ftl">
		</#if>
	<#else>
	没有内容!!
	</#if>
</div>