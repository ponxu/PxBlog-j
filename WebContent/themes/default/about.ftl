<#import "lib/body.ftl" as px>
<@px.body>

<div id="main_content">
	<#if post??>
		<div class="post_list hava_red_a_black" style="width: 930px;">
			<div class="abs">
				${post.post_content}
			</div>
		</div>
	<#else>
	没有内容!!
	</#if>
</div>

</@px.body>