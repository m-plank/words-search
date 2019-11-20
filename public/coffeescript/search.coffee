	#						XMLHttpRequest = require("xmlhttprequest").XMLHttpRequest

class SearchResults
    constructor: (@word, @usages) ->
    toHtml: ->
        highLightWord = (str, word) -> str.replace new RegExp("(#{word})", 'gi'), '<strong>$1</strong>'
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
				else
					console.log('no gain no pain')
					$('wiki-examples').innerHTML = "ERROR: #{xhr.responseText}"
		xhr.send null



@$ = (id) -> document.getElementById(id)

@doSearch = ->

	word = $("searchQuery").value
	appender = (data) ->
		if !data.isEmpty()
			$('wiki-examples').innerHTML = ''
			data.toHtml().forEach (x) ->
				$('wiki-examples').innerHTML += "#{x}"
		else
			$('wiki-examples').innerHTML = 'no results'
	if !!word
		$('wiki-examples').innerHTML = 'searching ...'
		(new API).search word, appender
	else
		$('wiki-examples').innerHTML = ''





# search = new API
# search.search('reload', (x) -> console.log x.toHtml())
