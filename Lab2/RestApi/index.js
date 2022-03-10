const express = require('express')
const hbs = require('hbs')
const path = require('path')

const apiData = require('./utils/apiData')

const port = process.env.PORT || 8080

const publicStaticDirPath = path.join(__dirname, './public')
const viewsPath = path.join(__dirname, './templates/views')
const partialsPath = path.join(__dirname, './templates/partials')

const app = express()

app.set("view engine", "hbs")
app.set("views", viewsPath)
hbs.registerPartials(partialsPath)
app.use(express.static(publicStaticDirPath))

app.get('', (req, res) => {
    res.render('index', {
        title: 'API'
    })
})

app.get('/api', (req, res) => {
    const apiType = req.query.api
    const question = req.query.quest

    if(!apiType) {
        res.send({
            error: "You need to choose api type"
        })
    }

    if(!question) {
        res.send({
            error: "You need to place your question to api"
        })
    }

    apiData(apiType, question, (error, {info1, info2, info3} = {}) => {
        if (error) {
            return res.send({
                error
            })
        }

        res.send({
            info1,
            info2,
            info3
        })
    })


})


app.get('*', (req, res) => {
    res.render('404', {
        title: "404: Page Not Found"
    })
})


app.listen(port, () => {
    console.log(`app at http://localhost:${port}`);
})

