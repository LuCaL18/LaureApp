package com.uniba.mobile.cddgl.laureapp.ui.ticket.impl;

import com.uniba.mobile.cddgl.laureapp.ui.ticket.TicketViewModel;
import com.uniba.mobile.cddgl.laureapp.ui.ticket.interfaces.TicketItemClickCallback;
/**
 * Implementazione dell'interfaccia TicketItemClickCallback
 */
public class TicketItemClickCallbackImpl implements TicketItemClickCallback {

    private final TicketViewModel ticketViewModel;

    public TicketItemClickCallbackImpl(TicketViewModel ticketViewModel) {
        this.ticketViewModel = ticketViewModel;
    }

    @Override
    public void onTicketClicked(String id) {
        ticketViewModel.init(id);
    }
}
