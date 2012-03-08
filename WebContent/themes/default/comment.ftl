<div id="comments" class="hava_red_a_black">
	<#if comments?has_content>
	<ul>
		<#list comments as c>
		<li id="li${c.comment_id}">
			<div class="commentleft">
				<div class="info">${c_index+1} F</div>
				<div class="gravatar">
					<a href="${c.comment_author_url}" target="_blank">
					<img src="http://www.gravatar.com/avatar/${StringUtils.md5(c.comment_author_email)}?s=36&d=monsterid">
					</a>
				</div>
			</div>
			<div class="commentright">
				<p class="info"><a href="#comment_add" onclick="refer('li${c.comment_id}')" style="float: right; display: none;">引用</a>By: ${c.comment_author}&nbsp;&nbsp;&nbsp;When: ${c.comment_date}</p>
				<div class="commenttext">${c.comment_content}</div>
			</div>
			<div class="clear"></div>
		</li>
		</#list>
	</ul>
	<#else>
	</#if>
</div>