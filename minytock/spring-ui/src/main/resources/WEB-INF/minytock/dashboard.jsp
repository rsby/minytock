<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE HTML>

<html>
	<head>
		<title>minytock</title>
		<link rel="stylesheet" href="http://twitter.github.com/bootstrap/assets/css/bootstrap.css">
		<style>
			.editor {
			    height: 300px;
			    position: absolute;
			}
		</style>
	</head>
	<body>
	
		<div class="navbar navbar-inverse navbar-fixed-top">
	      <div class="navbar-inner">
	        <div class="container">
	          <a class="brand" href="./dashboard">minytock</a>
	        </div>
	      </div>
	    </div>
	    
	    <hr class="bs-docs-separator">

		<div id="codeDivContainer" class="container">
		
			<div class="page-header">
				<h1>dashboard</h1>
			</div>
			
			<button class="btn btn-success">delegate</button>
			
			<hr class="bs-docs-separator">
			
			<div id="codeDiv" class="editor">
			</div>
			
		</div>
	    
	    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
    	<script src="http://twitter.github.com/bootstrap/assets/js/bootstrap.js"></script>
		<script src="http://d1n0x3qji82z53.cloudfront.net/src-min-noconflict/ace.js" type="text/javascript" charset="utf-8"></script>
		<script>
		
			$("#codeDiv").width($("#codeDivContainer").width());
		
			var editor = ace.edit("codeDiv");
			editor.setTheme("ace/theme/monokai");
		    editor.getSession().setMode("ace/mode/java");
		    
		    $(window).resize(function(){
		        $("#codeDiv").width($("#codeDivContainer").width());
		    });
		    
		</script>
	</body>
</html>