	#						XMLHttpRequest = require("xmlhttprequest").XMLHttpRequest

class SearchResults
    constructor: (@word, @usages) ->
    toHtml: ->
        highLightWord = (str, word) -> str.replace new RegExp(word, 'gi'), "<strong>#{word}</strong>"
        for example in @usages
            "<p>#{highLightWord example, @word}</p>"
    isEmpty: -> @usages.length == 0


class API
	search: (keyword, callback) ->
		xhr = new XMLHttpRequest
		xhr.open "GET", "http://localhost:8080/api/get-word-usages/#{encodeURI(keyword)}", true
		xhr.onreadystatechange = ->
			if xhr.readyState is 4
				if xhr.status is 200
					response = JSON.parse xhr.responseText
					results = response.usages
					callback new SearchResults(response.word, response.usages)
		xhr.send null


@doSearch = ->
	$ = (id) -> document.getElementById(id)
	word = $("searchQuery").value
	console.log("WORD:#{word}")
	appender = (data) ->
		console.log data
		if !data.isEmpty()
			console.log data
			$('wiki-examples').innerHTML = ''
			data.toHtml().forEach (x) ->
				$('wiki-examples').innerHTML += "#{x}"
		else
			console.log 'empty result'
			$('wiki-examples').innerHTML = 'no results'

	(new API).search word, appender





# search = new API
# search.search('reload', (x) -> console.log x.toHtml())
