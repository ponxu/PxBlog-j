<div id="main_content">
	<#if tags?has_content>
	<ul class="tag_list hava_red_a_black">
	<#list tags as t>
		<li>
			<a href="${blog.g('blogurl')}/tag/${t.term_taxonomy_id}/${t.name}" style="font-size: ${t.count?number/5 + 15}px; color: rgb(${random.nextInt(255)}, ${random.nextInt(255)}, ${random.nextInt(255)});">${t.name}
			</a>
		</li>
	</#list>
	</ul>
	<#else>
	没有内容!!
	</#if>
</div>