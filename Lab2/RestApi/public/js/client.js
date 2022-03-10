var fetchApi = '/api'

const questionForm = document.querySelector('form')
const search = document.querySelector('input')
const apiType = document.querySelector('select')

const info1 = document.querySelector('.info1 span')
const info2 = document.querySelector('.info2')
const info3 = document.querySelector('.info3')


questionForm.addEventListener('submit', (event) => {
    event.preventDefault()

    if(!search.value) {
        info1.textContent = "You have to ask about something"
        info2.textContent = ""
        info3.textContent = ""  
        return
    }

    info1.textContent = "Loading..."
    info2.textContent = ""
    info3.textContent = ""

    const apiDataAddress = fetchApi + "?quest=" + search.value + "&api=" + apiType.value

    fetch(apiDataAddress).then(response => {
        response.json().then(data => {
            if (data.error) {
                console.log(error)
            } else {
                info1.textContent = data.info1
                info2.textContent = data.info2
                info3.textContent = data.info3
            }
        })
    })


})