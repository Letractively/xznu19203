<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page language="java" pageEncoding="GBK"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>TreeMenu</title>
	<script type="text/javascript">var IMGDIR = 'images';var attackevasive = '0';</script>
	<link rel="StyleSheet" href="css/style_2_common.css" type="text/css" />
	<script type="text/javascript" src="js/tree.js"></script>
	<script type="text/javascript" src="js/common.js"></script>
	<script type="text/javascript" src="js/iframe.js"></script>
	<style type="text/css">HTML {
	BACKGROUND: #fff
	}
	BODY {
		PADDING-RIGHT: 2px; PADDING-LEFT: 2px; BACKGROUND: #fff; PADDING-BOTTOM: 2px; MARGIN: 0px; PADDING-TOP: 2px
	}
	.out {
		PADDING-RIGHT: 0.2em; PADDING-LEFT: 0.2em; OVERFLOW-X: hidden; PADDING-BOTTOM: 0.2em; OVERFLOW: auto; WIDTH: 96%; PADDING-TOP: 0.2em; HEIGHT: 100%; TEXT-ALIGN: left
	}
	.tree {
		PADDING-LEFT: 0.4em; FONT: 12px Verdana,Helvetica,Arial,sans-serif; COLOR: #666; WHITE-SPACE: nowrap
	}
	.tree IMG {
		BORDER-TOP-WIDTH: 0px; BORDER-LEFT-WIDTH: 0px; BORDER-BOTTOM-WIDTH: 0px; VERTICAL-ALIGN: middle; BORDER-RIGHT-WIDTH: 0px
	}
	.tree A.node {
		PADDING-RIGHT: 2px; PADDING-LEFT: 2px; PADDING-BOTTOM: 1px; PADDING-TOP: 1px; WHITE-SPACE: nowrap
	}
	.tree A.checked {
		PADDING-RIGHT: 2px; PADDING-LEFT: 2px; PADDING-BOTTOM: 1px; PADDING-TOP: 1px; WHITE-SPACE: nowrap
	}
	.tree A.node:hover {
		TEXT-DECORATION: underline
	}
	.tree A.checked:hover {
		TEXT-DECORATION: underline
	}
	.tree A.checked {
		COLOR: #444
	}
	.tree .clip {
		OVERFLOW: hidden
	}
	H1 {
		PADDING-LEFT: 10px; FONT-WEIGHT: bold; LINE-HEIGHT: 2.4em; HEIGHT: 2.4em
	}
	H2 {
		PADDING-LEFT: 10px; FONT-WEIGHT: normal; MARGIN-BOTTOM: 2em; BORDER-BOTTOM: #ddd 1px solid
	}
	H3 {
		PADDING-RIGHT: 5px; MARGIN-TOP: 2em; PADDING-LEFT: 5px; FONT-WEIGHT: normal; PADDING-BOTTOM: 3px; PADDING-TOP: 0.4em
	}
	STRONG {
		FONT-WEIGHT: bold
	}
	</style>
</head>

<body>
<div class="out">
	<script type="text/javascript">
	var tree = new dzTree('tree');
	tree.addNode(0, -1, '论坛首页', 'main.jsp', 'main', true);
	tree.addNode(99999999, 0, '查看新帖', 'succ.jsp', 'main', true);
	tree.addNode(24, 0, '新手入门', 'index.php?gid=24', 'main', false);
	tree.addNode(57, 0, '蚁族', 'index.php?gid=57', 'main', false);
	tree.addNode(20, 0, '蚁民', 'index.php?gid=20', 'main', false);
	tree.addNode(70, 24, '新人报道', 'forumdisplay.php?fid=70', 'main', false);
	tree.addNode(80, 24, '意见征集', 'forumdisplay.php?fid=80', 'main', false);
	tree.addNode(72, 24, '论坛公告', 'forumdisplay.php?fid=72', 'main', false);
	tree.addNode(23, 57, '蚂蚁说事', 'forumdisplay.php?fid=23', 'main', false);
	tree.addNode(59, 57, '蚂蚁上树', 'forumdisplay.php?fid=59', 'main', false);
	tree.addNode(21, 20, '蚁民爆料', 'forumdisplay.php?fid=21', 'main', false);
	tree.addNode(60, 57, '我爱蚁穴', 'forumdisplay.php?fid=60', 'main', false);
	tree.addNode(22, 20, '蚂蚁编辑部', 'forumdisplay.php?fid=22', 'main', false);
	tree.addNode(61, 57, '蚁族原生态', 'forumdisplay.php?fid=61', 'main', false);
	tree.addNode(62, 57, '蚁往情深', 'forumdisplay.php?fid=62', 'main', false);
	tree.addNode(76, 57, '蚁想天开', 'forumdisplay.php?fid=76', 'main', false);
	tree.addNode(78, 57, '蚂蚁大夫', 'forumdisplay.php?fid=78', 'main', false);
	tree.addNode(79, 57, '蚂蚁晒图', 'forumdisplay.php?fid=79', 'main', false);
	tree.addNode(92, 59, '职场路上', 'forumdisplay.php?fid=92', 'main', false);
	tree.addNode(82, 76, '娱乐八卦', 'forumdisplay.php?fid=82', 'main', false);
	tree.addNode(91, 59, '考试一族', 'forumdisplay.php?fid=91', 'main', false);
	tree.show();
	</script>
	</div>
</body>

</html>