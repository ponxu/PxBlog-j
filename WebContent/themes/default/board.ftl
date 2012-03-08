<#import "lib/body.ftl" as px>
<@px.body>

<div id="main_content">
	<#if post??>
		<div class="post_list hava_red_a_black" style="width: 930px;">
			<div class="abs">
				${post.post_content}
			</div>
		</div>
		<#if post.comment_status='open'>
			<p class="info hava_red_a_black">
				<a href="#comment_add">我要留言</a>
			</p>
			<#include "comment.ftl">
			<p class="info hava_red_a_black">
				<a href="#" onclick="loadComments()">刷新</a>
			</p>
			<#include "in/comment_form.ftl">
		</#if>
	<#else>
	没有内容!!
	</#if>
</div>

</@px.body>