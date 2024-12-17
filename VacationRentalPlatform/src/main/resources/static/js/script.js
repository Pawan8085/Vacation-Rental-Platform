const toggleSideBar = () => {

	if ($('.sidebar').is(":visible")) {
		// hide the sidebar
		$(".sidebar").css("display", "none");
		$(".content").css("margin-left", "0%");
	} else {
		// show the sidebar
		$(".sidebar").css("display", "block");
		$(".content").css("margin-left", "20%");
	}


};

const search = (currentPage) => {
	
	

	let query = $("#search-input").val();

	if (query == "") {
		$(".search-result").hide();
	} else {
		// sending request to server 
		let url = `http://localhost:8080/search/${query}`;

		fetch(url)
			.then((response) => {
				return response.json();
			}).then((data) => {

				// empty previous record
				$(".search-result").empty();
				if (data.length == 0) {
					$(".search-result").hide();
				}

				console.log(data)
				data.forEach(property => {
					let ele = $(`<a href='/host/property/${property.porpertyId}?currentPage=${currentPage}'></a>`).text(property.propertyName);

					ele.css({
						'text-decoration': 'none',
						'color': 'gray'

					});

					$(".search-result").append(ele, $("<br>"));
				});
			});
		$(".search-result").show();
	}
}

const locationFilter = (currentPage) => {
	let location = $('#locationInput').val();

	let url = `/0/location/${location}`;
	window.location.href = url;

}

const hostFilter = (currentPage) => {

	let host = $("#hostInput").val();
	window.location.href = `/0/host/${host}`;
}
const propertyFilter = (currentPage) => {

	let propertyType = $("#propertyType").val();

	if (propertyType == '') {
		return;
	}

	window.location.href = `/0/property/${propertyType}`

}
const viewProperty = (propertyId) => {

	console.log(propertyId)
	window.location.href = "/property/" + propertyId;
}

const locationSorting = (currentPage) => {
	console.log(currentPage)
	window.location.href = `/0/sort/location`;
}

const propertyTypeSorting = (currentPage) => {

	window.location.href = `/0/sort/property-type`

}

const searchProperty = (currentPage) => {

	let keyWord = $("#mysearch").val();
	if (keyWord == '') {
		retuern;
	}

	window.location.href = `/0/search/${keyWord}`;

}

function deleteProperty(propertyId, currentPage) {
	swal({
		title: "Are you sure?",
		text: "you want to delete this property!",
		icon: "warning",
		buttons: true,
		dangerMode: true,
	})
		.then((willDelete) => {
			if (willDelete) {

				window.location = '/host/delete-property/'+propertyId+'?currentPage='+currentPage;
			} else {
				swal("your property is safe");
			}
		});

}