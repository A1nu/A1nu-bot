const errorMessages = {
    permissions: window.modalDataErrorText.permissions,
    configDisabled: window.modalDataErrorText.disabled
}


angular.module('pocketModal', [])
    .constant("moment", moment)
    .service('PocketService', ['$http', function ($http) {
        this.getPockets = () => {
            return $http({
                method: 'GET',
                url: '/api/configuration/members-pockets?guildId='+  window.guildId,
                headers: 'Accept:application/json'
            }).then((res) => {
                return res.data
            })
        }

        this.sendTransaction = (guildId, memberId, description, diff, frozen, pocketId) => {
            return $http({
                method: 'POST',
                url: '/api/configuration/save-pocket',
                data: {
                    [window.csrf.parameterName]: window.csrf.token,
                    guildId: guildId,
                    memberId,
                    description,
                    diff,
                    frozen,
                    pocketId
                },
                headers: {
                    [window.csrf.headerName]: window.csrf.token
                }
            })
        }
    }])
    .controller('PocketController', ['$scope', 'PocketService', 'moment', function ($scope, PocketService, moment) {

        angular.extend($scope, {
            showError: false,
            showLoading: false,
            errorMessage: errorMessages.configDisabled,
            pocketsData: [],

            sendUpdate: (pocket, index) => {
                $scope.showLoading = true;
                $scope.showError = false;
                PocketService.sendTransaction(window.guildId, pocket.userId, pocket.description, pocket.diff, pocket.frozen, pocket.pocketId)
                    .then((suc) => {
                        $scope.showLoading = false;
                        $scope.pocketsData[index] = suc.data
                        console.log(suc.data)
                    }).catch((e) => {
                    $scope.showLoading = false
                    $scope.showError = true
                    $scope.errorMessage = `${e.data.errorMessage}. Error code: ${e.data.errorCode}`
                })
            },
            getPockets: () => {
                $scope.showLoading = true
                PocketService.getPockets()
                    .then((data) => {
                        $scope.showError = false
                        $scope.showLoading = false
                        $scope.pocketsData = data
                    }).catch(err => {
                    $scope.showLoading = false
                    $scope.showError = true
                    $scope.errorMessage = errorMessages[err.data.errorMessage]
                })
            },
            formatDiff: (diff) => {
                return diff > 0 ? '+' + diff.toString() : diff
            },
            formatDate: (date) => {
                if (!date) {
                    return ''
                }
                return moment(new Date(date)).format('MMM Do, hh:mm').toString()
            },
            openTransactionsModal: (userId) => {
                window.open(`/user-transactions?userId=${userId}&guildId=${window.guildId}`, '_blank', 'height=800,width=800,top=100,left=600')
            }
        })

        $('#pocketModal')
            .on('shown.bs.modal', function (event) {
                if (event.target.id === 'pocketModal') {
                    $scope.getPockets()
                }
            })
        $('#pocketModal')
            .on('hidden.bs.modal', function (event) {
                if (event.target.id === 'pocketModal') {
                    $scope.showError = false
                    $scope.showLoading = false
                    $scope.errorMessage = ''
                    $scope.pocketsData = []
                }
            })
}])
