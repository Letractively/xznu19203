/*
 * 文 件 名：MergeUtils.java
 * 版    权：Copyright 2008-2009 Huawei Tech.Co.Ltd.All Rights Reserved.
 * 描    述：
 * 修 改 人：tao.zhang
 * 修改时间：2010-4-26
 * 修改内容：新增
 */
package com.c_platform.parsebody.parsebody.sequence;

import java.util.ArrayList;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import com.c_platform.parsebody.parsebody.WebContext;
import com.c_platform.parsebody.parsebody.vips.XpathSimilarityPolicy;

/**
 * TODO 合并分块后的数据块
 * 
 * @author tao.zhang
 * @version C02 2010-4-26
 * @since OpenEye TAPS_AGENT V1R1C02
 */
public class MergePolicy {
	// private static Logger logger = Logger
	// .getLogger(MergePolicy.class.getName());

	private int MINI;

	private int SMALL; // 只用来判断第一个和最后一个节点

	private int BIG;

	private static final int MAXPAGENUM = 12;// 分页数量最大数

	public MergePolicy(int mini, int small, int big) {
		this.MINI = mini;
		this.SMALL = small;
		this.BIG = big;
	}

	/**
	 * desc 合并块 1/大小 2/块与块之间的相似度
	 * 
	 * @param list
	 * @param blockSize
	 * @param ctx
	 * @return ArrayList<Node[]>
	 */
	public ArrayList<Node[]> merge(ArrayList<Node[]> list, int blockSize,
			WebContext ctx) {
		/*
		 * //isRss合并为一页 if (ctx.isRss()){ while (list.size()>1){ int
		 * i=list.size()-1;// Node[] newNodes = null; Node[] thisNodes =
		 * list.get(i); Node[] last = list.get(i-1); if (thisNodes == null ||
		 * last == null) break; newNodes = mergeTwoNodesToOne(last,thisNodes);
		 * list.remove(i); list.remove(i-1); list.add(i-1, newNodes); } return
		 * list; }
		 */
		/*
		 * 先根据相似度合并为非mini节点，然后根据大小按照顺序合并
		 */
		if (list.size() < 2) {
			return list;
		}
		// 根据相似度合并
		while (!isAllNodesNotMini(list, blockSize, ctx)) {
			for (int i = 1; i < list.size();) {// 从第二个节点开始遍历，注意i的变化
				Node[] thisNodes = list.get(i);
				if (thisNodes == null)
					break;
				Node[] last = list.get(i - 1);
				if (list.size() <= i + 1) { // next为null，特殊处理，只判断last和this
					if (isMini(thisNodes, ctx) || isMini(last, ctx)) {// 合并last和this至last
						last = mergeTwoNodesToOne(last, thisNodes);
						list.remove(i);// 先移除最后一个，再移除倒数第二个
						list.remove(i - 1);
						list.add(i - 1, last);// 直接添加至list
					}
					break;
				}
				Node[] next = list.get(i + 1);
				if (isMini(thisNodes, ctx)) {
					if (isMini(last, ctx) && isMini(next, ctx)) {
						// 根据相似度合并至前后节点
						if (getSimBetweenNodes(last, thisNodes) >= getSimBetweenNodes(
								next, thisNodes)) {
							last = mergeTwoNodesToOne(last, thisNodes);
							list.remove(i);
							list.remove(i - 1);
							list.add(i - 1, last);
							continue;// i不变
						} else {
							thisNodes = mergeTwoNodesToOne(thisNodes, next);
							list.remove(i + 1);
							list.remove(i);
							list.add(i, thisNodes);
							i++;
							continue;// i++
						}
					} else if (getBlockSize(last, ctx) < getBlockSize(next, ctx)) {
						// 合并last与this至last
						last = mergeTwoNodesToOne(last, thisNodes);
						list.remove(i);
						list.remove(i - 1);
						list.add(i - 1, last);
						continue;// i不变
					} else {
						// 合并this与next至this
						thisNodes = mergeTwoNodesToOne(thisNodes, next);
						list.remove(i + 1);
						list.remove(i);
						list.add(i, thisNodes);
						i++;
						continue;// i++
					}
				}
				i++;
			}// end of for
			if (list.size() >= 2 && isSmall(list.get(0), blockSize, ctx)) {
				Node[] first = mergeTwoNodesToOne(list.get(0), list.get(1));
				list.remove(1);
				list.remove(0);
				list.add(0, first);
			}
		}// end of while
		// 根据大小从前至后合并
		for (int i = 0; i < list.size() - 1;) {
			Node[] thisNodes = list.get(i);
			Node[] next = list.get(i + 1);
			if (thisNodes == null || next == null)
				break;
			Node[] newNodes = mergeTwoNodesToOne(thisNodes, next);
			if (!isBig(newNodes, blockSize, ctx)) {
				list.remove(i + 1);
				list.remove(i);
				list.add(i, newNodes);
			} else {
				i++;
			}
		}
		// 处理最后一个节点
		if (list.size() >= 2) {
			int s = list.size() - 1;
			if (isSmall(list.get(s), blockSize, ctx)
					&& !isBig(list.get(s - 1), blockSize, ctx)) {
				Node[] piror = mergeTwoNodesToOne(list.get(s - 1), list.get(s));
				list.remove(s);
				list.remove(s - 1);
				list.add(piror);
			}
		}
		// 处理大于MAXPAGENUM页的list
		while (list.size() > MAXPAGENUM) {
			int tag = 0;
			Node[] newNodes;
			double minListSize = getBlockSize(mergeTwoNodesToOne(list.get(0),
					list.get(1)), ctx);
			for (int i = 0; i < list.size() - 1; i++) {
				Node[] thisNodes = list.get(i);
				Node[] next = list.get(i + 1);
				if (thisNodes == null || next == null)
					break;
				newNodes = mergeTwoNodesToOne(thisNodes, next);
				if (minListSize > getBlockSize(newNodes, ctx)) {
					minListSize = getBlockSize(newNodes, ctx);
					tag = i;// 要合并的块
				}
			}
			if (tag < list.size()) {
				newNodes = mergeTwoNodesToOne(list.get(tag), list.get(tag + 1));
				list.remove(tag + 1);
				list.remove(tag);
				list.add(tag, newNodes);
			}
		}
		// 统计输出文本的长度、图片的数量
		/*
		 * for (Node[] ns : list) { logger.info("合并后块大小：" + getBlockSize(ns,
		 * ctx)); }
		 */
		return list;
	}

	/**
	 * TODO 处理过大块，拆分Node[]为几个子节点
	 * 
	 * @param ns
	 * @param blockSize
	 * @param ctx
	 * @return ArrayList<Node[]>
	 */
	public ArrayList<Node[]> partitionBigBlock(Node[] ns, int blockSize,
			WebContext ctx) {
		ArrayList<Node[]> result = new ArrayList<Node[]>();
		if (ns == null || ns.length <= 0)
			return null;
		if (!isBig(ns, blockSize, ctx) || ns.length != 1 || ns[0] == null) {
			result.add(ns);
		} else {
			treeWalkBySize(ns[0], ctx, result);
			// 为了使能够使body下的文本节点取到xpath
			// 如http://www.txshuku.com/下的小说正文内容
			ArrayList<Node[]> newResult = new ArrayList<Node[]>();
			for (Node[] nodes : result) {
				if (nodes[0].getNodeType() == Node.TEXT_NODE) {
					Element e = DocumentHelper.createElement("DIV");
					e.add(nodes[0].detach());
					nodes[0] = e;
				}
				newResult.add(nodes);
			}
			result = newResult;
		}
		return result;
	}

	/**
	 * TODO 递归处理过大块
	 * 
	 * @param node
	 * @param ctx
	 * @return
	 */
	private void treeWalkBySize(Node node, WebContext ctx,
			ArrayList<Node[]> result) {
		if (node == null)
			return;
		if (node.getNodeType() != Node.ELEMENT_NODE) {
			result.add(new Node[] { node });
			return;
		}
		Element element = (Element) node;
		/*
		 * if ("table".equalsIgnoreCase(element.getName()) ||
		 * "ul".equalsIgnoreCase(element.getName())) { //
		 * 后期对table,ul可分，但要做标签补齐处理，标签补齐后标签ID相同如何解决 }
		 */
		String url = ctx.getUrl();
		if (url.toLowerCase().indexOf("qidian.com") != -1) {
			// 特殊处理起点中文的form可分
			if ("textarea".equalsIgnoreCase(element.getName())
					|| "select".equalsIgnoreCase(element.getName())
					|| "script".equalsIgnoreCase(element.getName())
					|| "table".equalsIgnoreCase(element.getName())
					|| "ul".equalsIgnoreCase(element.getName())
					|| getNodeSize(element, ctx) <= BIG) {
				result.add(new Node[] { element });
				return;
			}
		} else if (url.toLowerCase().indexOf("finance.sina.com") != -1
				|| url.toLowerCase().indexOf("bbs.ifeng.com") != -1
				|| url.toLowerCase().indexOf("www.sohu.com") != -1) {
			// 特殊处理上面两个网站的ul可分，新浪财经的li也必须为可分
			if ("form".equalsIgnoreCase(element.getName())
					|| "textarea".equalsIgnoreCase(element.getName())
					|| "select".equalsIgnoreCase(element.getName())
					|| "script".equalsIgnoreCase(element.getName())
					|| "table".equalsIgnoreCase(element.getName())
					// || "li".equalsIgnoreCase(element.getName())
					|| getNodeSize(element, ctx) <= BIG) {
				result.add(new Node[] { element });
				return;
			}
		} else if ("form".equalsIgnoreCase(element.getName())
				|| "textarea".equalsIgnoreCase(element.getName())
				|| "select".equalsIgnoreCase(element.getName())
				|| "script".equalsIgnoreCase(element.getName())
				|| "table".equalsIgnoreCase(element.getName())
				|| "ul".equalsIgnoreCase(element.getName())
				|| getNodeSize(element, ctx) <= BIG) {
			result.add(new Node[] { element });
			return;
		}
		for (int i = 0; i < element.nodeCount(); i++) {
			treeWalkBySize(element.node(i), ctx, result);
		}
	}

	/**
	 * TODO 判断所有的节点是否为合适块,即无法再合并
	 * 
	 * @param list
	 * @param blockSize
	 * @param ctx
	 * @return boolean
	 */
	private boolean isAllNodesNotMini(ArrayList<Node[]> list, int blockSize,
			WebContext ctx) {
		if (list == null || list.size() <= 0 || blockSize <= 0)
			return false;
		if (list.size() == 1)
			return true;
		for (Node[] aList : list) {
			if (aList == null) {
				continue;
			}
			if (isMini(aList, ctx)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * TODO 判断是否为大块
	 * 
	 * @param ns
	 * @param blockSize
	 * @param ctx
	 * @return boolean
	 */
	public boolean isBig(Node[] ns, int blockSize, WebContext ctx) {
		SequenceUtils su = new SequenceUtils();
		if (ns == null || ns.length == 0 || blockSize <= 0)
			return false;
		int textLength = 0;
		int imgSize = 0;
		for (Node n : ns) {
			if (n == null) {
				continue;
			}
			textLength += su.getTextLength(n);
			if (n instanceof Element) {
				imgSize += (int) su.getElementImgSize((Element) n, ctx);
			}
		}
		textLength += imgSize;
		if (textLength > BIG) {
			return true;
		}
		return false;
	}

	/**
	 * desc 判断是否为小块
	 * 
	 * @param ns
	 * @param blockSize
	 * @param ctx
	 * @return boolean
	 */
	private boolean isSmall(Node[] ns, int blockSize, WebContext ctx) {
		if (ns == null || ns.length == 0 || blockSize <= 0)
			return false;
		SequenceUtils su = new SequenceUtils();
		int textLength = 0;
		int imgSize = 0;
		for (Node n : ns) {
			if (n == null) {
				continue;
			}
			textLength += su.getTextLength(n);
			if (n instanceof Element) {
				imgSize += (int) su.getElementImgSize((Element) n, ctx);
			}
		}
		textLength += imgSize;
		if (textLength <= SMALL) {
			return true;
		}
		return false;
	}

	/**
	 * desc 判断是否为微小块
	 * 
	 * @param ns
	 * @param ctx
	 * @return boolean
	 */
	private boolean isMini(Node[] ns, WebContext ctx) {
		if (ns == null || ns.length == 0)
			return false;
		SequenceUtils su = new SequenceUtils();
		int textLength = 0;
		int imgSize = 0;
		for (Node n : ns) {
			if (n == null) {
				continue;
			}
			textLength += su.getTextLength(n);
			if (n instanceof Element) {
				imgSize += (int) su.getElementImgSize((Element) n, ctx);
			}
		}
		textLength += imgSize;
		if (textLength < MINI || ctx.isIframe()|| ctx.isRss()) {
			return true;
		}
		return false;
	}

	/**
	 * TODO 合并两个Node[]至一个Node[],注意前后顺序的区别
	 * 
	 * @param ns1
	 * @param ns2
	 *            []
	 * @return Node[]
	 */
	private Node[] mergeTwoNodesToOne(Node[] ns1, Node[] ns2) {
		if (ns1 == null || ns2 == null || ns1.length == 0 || ns2.length == 0)
			return null;
		Node[] result = new Node[ns1.length + ns2.length];
		System.arraycopy(ns1, 0, result, 0, ns1.length);
		System.arraycopy(ns2, 0, result, ns1.length, ns2.length);
		return result;
	}

	/**
	 * TODO 获得块与块之间的相似度,取节点与节点之间的最小值
	 * 
	 * @param ns1
	 * @param ns2
	 *            []
	 * @return int
	 */
	private int getSimBetweenNodes(Node[] ns1, Node[] ns2) {
		if (ns1 == null || ns2 == null)
			return 0;
		if (ns1.length <= 0 || ns2.length <= 0)
			return 0;
		int sim = 100;
		XpathSimilarityPolicy xsp = new XpathSimilarityPolicy();
		for (Node n1 : ns1) {
			if (n1 == null)
				continue;
			for (Node n2 : ns2) {
				if (n2 == null)
					continue;
				int s = xsp.praseXpathSimilarity(n1.getUniquePath(), n2
						.getUniquePath());
				sim = Math.min(sim, s);
			}
		}
		return sim;
	}

	/**
	 * TODO 获得某一块（页面）的大小
	 * 
	 * @param ns
	 * @param ctx
	 * @return double
	 */
	private double getBlockSize(Node[] ns, WebContext ctx) {
		SequenceUtils su = new SequenceUtils();
		double txtLength = 0;
		double imgSize = 0;
		for (Node n : ns) {
			if (n == null)
				continue;
			txtLength += su.getTextLength(n);
			if (n instanceof Element) {
				imgSize += su.getElementImgSize((Element) n, ctx);
			}
		}
		txtLength += imgSize;
		return txtLength;
	}

	/**
	 * TODO 获得某一节点的文本长度（包括图片）
	 * 
	 * @param n
	 * @param ctx
	 * @return double
	 */
	private double getNodeSize(Node n, WebContext ctx) {
		if (n == null)
			return 0;
		SequenceUtils su = new SequenceUtils();
		int textLength = su.getTextLength(n);
		if (n.getNodeType() != Node.ELEMENT_NODE) {
			return textLength;
		}
		double imgSize = su.getElementImgSize((Element) n, ctx);
		textLength += imgSize;
		return textLength;
	}

	/**
	 * TODO 特殊拆分table和ul，并补齐标签
	 * 
	 * @param e
	 * @param ctx
	 * @return ArrayList<Node[]>
	 */
	@SuppressWarnings("unused")
	private ArrayList<Node[]> treeWalkTableUl(Element e, WebContext ctx) {
		if (!"table".equalsIgnoreCase(e.getName())
				|| !"ul".equalsIgnoreCase(e.getName())) {
			return null;
		}
		ArrayList<Node[]> result = new ArrayList<Node[]>();
		int tag = 0;
		for (int i = 0; i < e.nodeCount(); i++) {
			if (getNodeSize(e.node(i), ctx) <= BIG) {
				// 移除其所有的子节点，然后将其逐一添加到父节点下，作为list返回
				tag = 1;
				break;
			} else {
				// 遍历其孙节点
			}
		}
		if (tag == 1) {
			for (int i = 0; i < e.nodeCount(); i++) {
				// result.add(e.node(i));
				e.remove(e.node(i));
				if (getNodeSize(e.node(i), ctx) > BIG) {

				}
			}
		}
		return result;
	}

}
