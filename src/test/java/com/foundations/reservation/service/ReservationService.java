package com.foundations.reservation.service;

import com.foundations.common.AbstractBaseService;
import com.foundations.common.config.Configuration;
import com.foundations.common.config.TestConstants;
import com.foundations.common.model.AuthRequest;
import com.foundations.common.model.AuthResponse;
import com.foundations.common.model.CreateBookingRequest;
import com.foundations.common.model.CreateBookingResponse;
import com.foundations.common.model.Reservations;
import org.apache.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservationService extends AbstractBaseService {

    public static final String authPath = "/auth";
    public static final String createBookingPath = "/booking";
    public static final String deleteBookingPath = "/booking/";
    private final Reservations reservations;

    public ReservationService(Configuration configuration, Reservations reservations) {
        super(configuration.getProperties().getProperty("base.path"));
        this.reservations = reservations;
    }

    public void getAuthKey(AuthRequest auth) {
        var result = post(authPath, null, auth);
        assertThat(result.statusCode()).isEqualTo(HttpStatus.SC_OK);
        reservations.setAuthResponse(result.response().as(AuthResponse.class));
    }

    public void createBooking(CreateBookingRequest bookingRequest) {
        var result = post(createBookingPath, null, bookingRequest);
        assertThat(result.statusCode()).isEqualTo(HttpStatus.SC_OK);
        reservations.setCreateBookingResponse(result.response().as(CreateBookingResponse.class));
    }

    public void cancelHotelReservation() {
        String bookingId = reservations.getCreateBookingResponse().getBookingid();
        String token = reservations.getAuthResponse().getToken();
        var result = delete(deleteBookingPath + bookingId, createHttpHeaders(token), null);
        assertThat(result.statusCode()).isEqualTo(HttpStatus.SC_CREATED);
    }

    // This method asserts most part of the createBooking response
    public void assertBookingCreation() {
        assertThat(reservations.getCreateBookingResponse().getBookingid()).isNotEmpty();
        assertThat(reservations.getCreateBookingResponse().getBooking().getFirstname()).isEqualTo(
                TestConstants.firstName);
        assertThat(reservations.getCreateBookingResponse().getBooking().getLastname()).isEqualTo(
                TestConstants.lastName);
        assertThat(reservations.getCreateBookingResponse().getBooking().getTotalprice()).isEqualTo(
                TestConstants.totalPrice);
        assertThat(reservations.getCreateBookingResponse().getBooking().getDepositpaid()).isEqualTo(
                TestConstants.depositPaid);
        assertThat(reservations.getCreateBookingResponse().getBooking().getAdditionalneeds()).isEqualTo(
                TestConstants.additionalNeeds);
    }
}
