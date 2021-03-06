package filesutra

import grails.converters.JSON

class AuthController {

    def authService
    def amazonService

    def google() {
        redirect(url: Google.getLoginUrl())
    }

    def dropbox() {
        redirect(url: Dropbox.getLoginUrl())
    }

    def box() {
        redirect(url: Box.getLoginUrl())
    }

    def onedrive() {
        redirect(url: Onedrive.getLoginUrl())
    }

    def amazon() {
        redirect(url: AmazonCloudDrive.getLoginUrl())
    }

    def googleCallback(String code) {
        if (code) {
            def accessInfo = Google.exchangeCode(code)
            def emailId = Google.getEmailId(accessInfo.accessToken)
            Access googleAccess = authService.googleLogin(emailId, accessInfo)
            if (googleAccess) {
                session.googleAccessId = googleAccess.id
            }
        }
        redirect(uri: '/picker#Google')
    }

    def dropboxCallback(String code) {
        if (code) {
            def accessInfo = Dropbox.exchangeCode(code)
            def emailId = Dropbox.getEmailId(accessInfo.accessToken)
            Access dropboxAccess = authService.dropboxLogin(emailId, accessInfo)
            if (dropboxAccess) {
                session.dropboxAccessId = dropboxAccess.id
            }
        }
        redirect(uri: '/picker#Dropbox')
    }

    def boxCallback(String code) {
        if (code) {
            def accessInfo = Box.exchangeCode(code)
            def emailId = Box.getEmailId(accessInfo.accessToken)
            Access boxAccess = authService.boxLogin(emailId, accessInfo)
            if (boxAccess) {
                session.boxAccessId = boxAccess.id
            }
        }
        redirect(uri: '/picker#Box')
    }

    def onedriveCallback(String code) {
        if (code) {
            def accessInfo = Onedrive.exchangeCode(code)
            def emailId = Onedrive.getEmailId(accessInfo.accessToken)
            Access onedriveAccess = authService.onedriveLogin(emailId, accessInfo)
            if (onedriveAccess) {
                session.onedriveAccessId = onedriveAccess.id
            }
        }
        redirect(uri: '/picker#OneDrive')
    }

    def amazonCallback(String code) {
        if (code) {
            def accessInfo = AmazonCloudDrive.exchangeCode(code)
            def endpoints = AmazonCloudDrive.getUserEndpoints(accessInfo.accessToken)
            session[AmazonCloudDriveAPIType.NODE.toString()] = endpoints.contentUrl
            session[AmazonCloudDriveAPIType.METADATA.toString()] = endpoints.metadataUrl
            def rootFolderId = AmazonCloudDrive.getRootFolderId(session[AmazonCloudDriveAPIType.METADATA.toString()], accessInfo.accessToken)
            Access amazonAccess = authService.amazonLogin(rootFolderId, accessInfo)
            if (amazonAccess) {
                session.amazonAccessId = amazonAccess.id
            }
        }
        redirect(uri: '/picker#AmazonCloudDrive')
    }

    def logout(String app) {
        switch (app) {
            case "Google":
                session.googleAccessId = null
                break
            case "Dropbox":
                session.dropboxAccessId = null
                break
            case "Box":
                session.boxAccessId = null
                break
            case "OneDrive":
                session.onedriveAccessId = null
                break
            case "AmazonCloudDrive":
                session.amazonAccessId = null
                session[AmazonCloudDriveAPIType.NODE.toString()] = null
                session[AmazonCloudDriveAPIType.METADATA.toString()] = null
                break
        }
        def resp = [success: true]
        render resp as JSON
    }
}
