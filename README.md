# Steam Investment Helper

## Table of contents
* [Introduction](#introduction)
* [Technologies](#built-with)
* [API](#api)
* [Features](#features)


## Introduction
Steam Investment Helper is a web application that allows users to monitor their investments on the Steam platform by tracking item price history, displaying data in charts and providing investment recommendations.

This repository contains backend written in Java / Spring Boot. Frontend created for this project can be found [here](https://github.com/patt-h/steam-investment-frontend)

Previously, this project was hosted on Microsoft Azure and was available at `steam-investment-helper-backend.azurewebsites.net`

## Built with
- Java 17
- Spring Boot 3.0.2
- PostgreSQL
- Microsoft Azure
- [Steam Market API](https://github.com/Revadike/InternalSteamWebAPI/wiki)

## API
API documentation is available in Swagger UI after starting the backend at:

`http://localhost:8080/swagger-ui/index.html`

Keep in mind that every endpoint is secured with JWT Bearer Token that can be received after login with `/login` endpoint.

## Features
- <b>Price Monitoring</b> - Track the current and history price of Steam items.
- <b>Charts</b> - Check price trends for individual items using line charts.
- <b>Dashboard</b> - All important information about investments is displayed in the form of dashboard.
- <b>User System</b> - User registration and authentication system using JWT.
- <b>Items recommendations</b> - Find new items to buy suggested by recommendation algorithms.
- <b>Goal selection</b> - Choose the goal you are raising money for and check how much you still need.
