
//layer 0
var dodag =
{
	text: {name: "dead:beef::1"}
};

//layer 1
var n1 =
{
	parent: dodag,
	text: {name: "fe80::dead:beef:abba:1"}
}
var n2 =
{
	parent: dodag,
	text: {name: "fe80::dead:beef:abba:2"}
}
var n3 =
{
	parent: dodag,
	text: {name: "fe80::dead:beef:abba:3"}
}
var n10 =
{
	parent: dodag,
	text: {name: "fe80::dead:beef:abba:a"}
}

//layer 2
var n11 =
{
	parent: n1,
	text: {name: "fe80::dead:beef:abba:b"}
}
var n5 =
{
	parent: n2,
	text: {name: "fe80::dead:beef:abba:5"}
}
var n12 =
{
	parent: n2,
	text: {name: "fe80::dead:beef:abba:c"}
}
var n4 =
{
	parent: n3,
	text: {name: "fe80::dead:beef:abba:4"}
}

//layer 3
var n6 =
{
	parent: n4,
	text: {name: "fe80::dead:beef:abba:6"}
}

//layer 4
var n7 =
{
	parent: n6,
	text: {name: "fe80::dead:beef:abba:7"}
}
var n8 =
{
	parent: n6,
	text: {name: "fe80::dead:beef:abba:8"}
}
var n9 =
{
	parent: n6,
	text: {name: "fe80::dead:beef:abba:9"}
}


var config = {
	container: "#dodag-tree",
	rootOrientation: "NORTH",
	levelSeparation: 75,
	connectors: {
		type: "step" //TODO this one or straight????
	},
};


var simple_chart_config = [
	config,
	dodag, n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12
];
