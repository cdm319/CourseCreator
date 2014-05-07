function toggleUnits(elem, child) {
	if (child.is(":visible")) {
		child.hide();
		elem.html("&plus");
	} else {
		child.show();
		elem.html("&minus");
	}
}