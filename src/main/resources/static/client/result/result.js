const resultApp = new Vue({
    el: '#result',
    data: {
        loading: false,
        userId: '',
        password: '',
        key: '',
        again: false,
        resultData: [],
        resultList: [],
        errorMessage: '',
        student: {
            name: '',
            className: '',
            userId: ''
        }
    },
    watch: {
        'again': function () {
            resultApp.searchResult();
        }
    },
    methods: {
        getResult: function () {
            resultApp.student = {
                name: '',
                className: '',
                userId: ''
            };
            resultApp.errorMessage = '';
            resultApp.resultData = [];
            resultApp.resultList = [];
            resultApp.loading = true;
            const apiAddress = window.location.host;
            $.get(`http://${apiAddress ? apiAddress : 'localhost:10000'}/getResultDirect`,
                {
                    userId: resultApp.userId,
                    password: resultApp.password,
                    key: resultApp.key
                },
                function (result) {
                    if (result.meta.success) {
                        resultApp.resultData = result.data[0];
                        resultApp.resultData = [...result.data];
                        resultApp.resultData.splice(0, 1);
                        resultApp.student = result.data[0];
                        resultApp.resultList = [...resultApp.resultData];
                        console.log(resultApp.resultData);
                    } else {
                        resultApp.errorMessage = result.meta.message;
                        console.log(resultApp.errorMessage);
                    }

                    resultApp.loading = false;
                }
            );
        },
        getInput: function () {
            resultApp.errorMessage = '';
        },
        searchResult: function () {
            console.log(resultApp.again);
            resultApp.resultList = [...resultApp.resultData].filter(function (element, index, self) {
                let againFlag =true;
                if (resultApp.again) {
                    againFlag = element['重修标记'].indexOf('重修') !== -1;
                }
                return element['课程名称'].indexOf(resultApp.key) !== -1 && againFlag;
            });
        }
    }
});