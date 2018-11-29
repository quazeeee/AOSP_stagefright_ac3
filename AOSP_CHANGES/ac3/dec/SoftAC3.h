/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#ifndef SOFT_AC3_H_

#define SOFT_AC3_H_

#include <media/stagefright/omx/SimpleSoftOMXComponent.h>

namespace android {

struct SoftAC3 : public SimpleSoftOMXComponent {
    SoftAC3(const char *name,
            const OMX_CALLBACKTYPE *callbacks,
            OMX_PTR appData,
            OMX_COMPONENTTYPE **component);

protected:
    virtual ~SoftAC3();

    virtual OMX_ERRORTYPE internalGetParameter(
            OMX_INDEXTYPE index, OMX_PTR params);

    virtual OMX_ERRORTYPE internalSetParameter(
            OMX_INDEXTYPE index, const OMX_PTR params);

    virtual void onQueueFilled(OMX_U32 portIndex);

    virtual void onPortFlushCompleted(OMX_U32 portIndex);
    virtual void onReset();

private:
    enum {
        kNumBuffers = 4,
        kMaxNumSamplesPerFrame = 16384,
    };

    bool mSignalledError;

    void initPorts();

    // static int DecodeAC3(gsm handle, int16_t *out, uint8_t *in, size_t inSize);

    DISALLOW_EVIL_CONSTRUCTORS(SoftAC3);
};

}  // namespace android

#endif  // SOFT_AC3_H_

