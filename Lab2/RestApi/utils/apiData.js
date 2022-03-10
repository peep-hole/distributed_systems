const constants = require('../config')
const request = require('request')

const apiData = (apiType, question, callback) => {
    if (apiType === "api2") {

        const url = constants.api2.BASE_URL + question

        request({url, json: true}, (error) => {
            if (error) {
                callback(undefined, {
                    info1: "Service is unavailable. Please try again later",
                    info2: "",
                    info3: ""
                    
                })
            }

        })

        request({url, json: true}, (error, {body}) => {

            if(error) {
                callback("Can't fetch data from open weather map api", undefined)
            } else if (!body.drinks) {
                callback(undefined, {
                    info1: "Can't find such drink in database, please try again...",
                    info2: "",
                    info3: ""
                    
                })
            } else {

                listOfIngredients = []

                if (body.drinks[0].strIngredient1) {
                    listOfIngredients.push(body.drinks[0].strIngredient1 + "( " + body.drinks[0].strMeasure1 + ")")
                }
                if (body.drinks[0].strIngredient2) {
                    listOfIngredients.push(body.drinks[0].strIngredient2 + "( " + body.drinks[0].strMeasure2 + ")")
                }                 
                if (body.drinks[0].strIngredient3) {
                    listOfIngredients.push(body.drinks[0].strIngredient3 + "( " + body.drinks[0].strMeasure3 + ")")
                }                 
                if (body.drinks[0].strIngredient4) {
                    listOfIngredients.push(body.drinks[0].strIngredient4 + "( " + body.drinks[0].strMeasure4 + ")")
                }                 
                if (body.drinks[0].strIngredient5) {
                    listOfIngredients.push(body.drinks[0].strIngredient5 + "( " + body.drinks[0].strMeasure5 + ")")
                }                 
                if (body.drinks[0].strIngredient6) {
                    listOfIngredients.push(body.drinks[0].strIngredient6 + "( " + body.drinks[0].strMeasure6 + ")")
                }                 


                callback(undefined, {
                    info1: "Name: " + body.drinks[0].strDrink,
                    info2: "Recipe: " + body.drinks[0].strInstructions,
                    info3: "Ingredients: " + listOfIngredients.toString()
                    
                })
            }
            
        })
    } else {
        const url = constants.api1.BASE_URL + question +"&from=2022-02-10&sortBy=publishedAt&apiKey=" + constants.api1.SECRET_KEY
        
        request({url, json: true}, (error) => {
            if (error) {
                callback(undefined, {
                    info1: "Service is unavailable. Please try again later",
                    info2: "",
                    info3: ""
                    
                })
            }

        })

        request({url, json: true}, (error, {body}) => {
            if (error) {
                callback("Can't fetch data from open weather map api", undefined)
            }else if (body.totalResults === 0) {
                callback(undefined, {
                    info1: "Can't find anything about what you ask, please try again...",
                    info2: "",
                    info3: ""
                    
                })
            } else {
                callback(undefined, {
                    info1: "Source: " + body.articles[0].source.name,
                    info2: "Author: " + body.articles[0].author,
                    info3: "Content: " + body.articles[0].content
                    
                })
            }
        })
    }
}

module.exports = apiData