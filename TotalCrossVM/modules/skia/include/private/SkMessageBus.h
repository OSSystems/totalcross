/*
 * Copyright 2013 Google Inc.
 *
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 */

#ifndef SkMessageBus_DEFINED
#define SkMessageBus_DEFINED

#include "../private/SkNoncopyable.h"
#include "SkMutex.h"
#include "SkOnce.h"
#include "SkTArray.h"
#include "SkTDArray.h"
#include "SkTypes.h"

/**
 * Message must implement bool Message::shouldSend(uint32_t inboxID) const. Perhaps someday we
 * can use std::experimental::is_detected to avoid this requirement by sending to all inboxes when
 * the method is not detected on Message.
 */
template <typename Message>
class SkMessageBus : SkNoncopyable {
public:
	// Post a message to be received by Inboxes for this Message type. Checks
	// Message::shouldSend() for each inbox. Threadsafe.
	static void Post(const Message& m);

	class Inbox {
	public:
		Inbox(uint32_t uniqueID = SK_InvalidUniqueID);
		~Inbox();

		// Overwrite out with all the messages we've received since the last call.  Threadsafe.
		void poll(SkTArray<Message>* out);

	private:
		SkTArray<Message>  fMessages;
		SkMutex            fMessagesMutex;
		uint32_t           fUniqueID;

		friend class SkMessageBus;
		void receive(const Message& m);  // SkMessageBus is a friend only to call this.
	};

private:
	SkMessageBus();
	static SkMessageBus* Get();

	SkTDArray<Inbox*> fInboxes;
	SkMutex           fInboxesMutex;
};

// This must go in a single .cpp file, not some .h, or we risk creating more than one global
// SkMessageBus per type when using shared libraries.  NOTE: at most one per file will compile.
#define DECLARE_SKMESSAGEBUS_MESSAGE(Message)                      \
	template <>                                                    \
	SkMessageBus<Message>* SkMessageBus<Message>::Get() {          \
		static SkOnce once;                                        \
		static SkMessageBus<Message>* bus;                         \
		once([] { bus = new SkMessageBus<Message>(); });           \
		return bus;                                                \
	}

//   ----------------------- Implementation of SkMessageBus::Inbox -----------------------

template<typename Message> SkMessageBus<Message>::Inbox::Inbox(uint32_t uniqueID) : fUniqueID(
		uniqueID) {
	// Register ourselves with the corresponding message bus.
	SkMessageBus<Message>* bus = SkMessageBus<Message>::Get();
	SkAutoMutexAcquire lock(bus->fInboxesMutex);
	bus->fInboxes.push_back(this);
}

template<typename Message> SkMessageBus<Message>::Inbox::~Inbox() {
	// Remove ourselves from the corresponding message bus.
	SkMessageBus<Message>* bus = SkMessageBus<Message>::Get();
	SkAutoMutexAcquire lock(bus->fInboxesMutex);
	// This is a cheaper fInboxes.remove(fInboxes.find(this)) when order doesn't matter.
	for (int i = 0; i < bus->fInboxes.count(); i++) {
		if (this == bus->fInboxes[i]) {
			bus->fInboxes.removeShuffle(i);
			break;
		}
	}
}

template<typename Message>
void SkMessageBus<Message>::Inbox::receive(const Message& m) {
	SkAutoMutexAcquire lock(fMessagesMutex);
	fMessages.push_back(m);
}

template<typename Message>
void SkMessageBus<Message>::Inbox::poll(SkTArray<Message>* messages) {
	SkASSERT(messages);
	messages->reset();
	SkAutoMutexAcquire lock(fMessagesMutex);
	fMessages.swap(*messages);
}

//   ----------------------- Implementation of SkMessageBus -----------------------

template <typename Message> SkMessageBus<Message>::SkMessageBus() {}

template <typename Message>
/*static*/ void SkMessageBus<Message>::Post(const Message& m) {
	SkMessageBus<Message>* bus = SkMessageBus<Message>::Get();
	SkAutoMutexAcquire lock(bus->fInboxesMutex);
	for (int i = 0; i < bus->fInboxes.count(); i++) {
		if (m.shouldSend(bus->fInboxes[i]->fUniqueID)) {
			bus->fInboxes[i]->receive(m);
		}
	}
}

#endif  // SkMessageBus_DEFINED
