<div id="main_content">
	<#if list?has_content>
	<#list list as p>
		<div class="post_list hava_red_a_black">
			<h3><a href="${blog.url(p)}">${p.post_title}</a></h3>
			<p class="info">
				时间: ${p.post_date?substring(0, 19)}
				&nbsp;&nbsp;&nbsp;
				评论: <a href="${blog.url(p)}#comments">${p.comment_count}</a>
			</p>
			<div class="abs">
				${p.post_content}
			</div>
			<p class="info">
				分类/标签:
				<#if post_terms?has_content>
					<#list post_terms as t>
						<#if t.object_id=p.id>
							<a href="${blog.g('blogurl')}/category/${t.term_taxonomy_id}/${t.name}">${t.name}</a>
						</#if>
					</#list>
				</#if>
			</p>
		</div>
	</#list>
	<#include "nav.ftl">
	<#else>
	没有内容!!
	</#if>
</div>