<#if list?has_content && page??>
<div id="page_nav" class="hava_red_a_black">
	<#if page.index!=1>
	<a href="${blog.g('blogurl')}/?pageIndex=${page.index - 1}">←前一页</a>
	</#if>

	${page.index} of ${page.count}

	<#if page.index < page.count>
	<a href="${blog.g('blogurl')}/?pageIndex=${page.index + 1}">后一页→</a>
	</#if>
</div>
</#if>